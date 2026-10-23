package com.bmsedge.asset.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Allocates permanent, database-sequenced public asset codes. */
@Component
public class AssetCodeGenerator implements ApplicationRunner {

    private static final String SEQUENCE = "asset_code_seq";
    private final JdbcTemplate jdbcTemplate;

    public AssetCodeGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("CREATE SEQUENCE IF NOT EXISTS " + SEQUENCE + " START WITH 1 INCREMENT BY 1");

        // Keep the sequence ahead of codes already present after a deployment or restore.
        // Preserve its current value even if the asset that used it was deleted.
        var sequenceState = jdbcTemplate.queryForMap(
                "SELECT last_value, is_called FROM asset_code_seq");
        long sequenceValue = ((Number) sequenceState.get("last_value")).longValue();
        boolean sequenceWasUsed = Boolean.TRUE.equals(sequenceState.get("is_called"));
        Long existingMaximum = jdbcTemplate.queryForObject("""
                SELECT COALESCE(MAX(CAST(SUBSTRING(asset_code FROM 5) AS BIGINT)), 0)
                FROM assets WHERE asset_code ~ '^AST-[0-9]+$'
                """, Long.class);
        long maximumExistingCode = existingMaximum == null ? 0 : existingMaximum;
        long currentMaximum = Math.max(sequenceWasUsed ? sequenceValue : 0, maximumExistingCode);
        jdbcTemplate.queryForObject(
                "SELECT setval('asset_code_seq', ?, ?)",
                Long.class,
                Math.max(currentMaximum, 1),
                currentMaximum > 0);

        List<String> missingCodes = jdbcTemplate.query(
                "SELECT asset_id FROM assets WHERE asset_code IS NULL OR BTRIM(asset_code) = '' ORDER BY created_at NULLS FIRST, asset_id",
                (resultSet, rowNum) -> resultSet.getString(1));

        for (String assetId : missingCodes) {
            String code = nextCode();
            jdbcTemplate.update(
                    "UPDATE assets SET asset_code = ? WHERE asset_id = ? AND (asset_code IS NULL OR BTRIM(asset_code) = '')",
                    code,
                    assetId);
        }

        jdbcTemplate.execute("CREATE UNIQUE INDEX IF NOT EXISTS ux_assets_asset_code ON assets (asset_code)");
        jdbcTemplate.execute("ALTER TABLE assets ALTER COLUMN asset_code SET NOT NULL");
    }

    public String nextCode() {
        Long next = jdbcTemplate.queryForObject("SELECT nextval('asset_code_seq')", Long.class);
        if (next == null || next < 1) {
            throw new IllegalStateException("Could not allocate an Asset ID");
        }
        return "AST-%04d".formatted(next);
    }
}
