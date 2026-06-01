package com.myproject.clinic.permissionmatrix;

import org.springframework.util.AntPathMatcher;

import java.util.List;

/**
 * Patterns documented in docs/security-public-endpoints.md (permitAll / filter skip)
 * used for BYPASS_ACTIVE warnings on the matrix UI.
 */
public final class PublicSecurityCatalog {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private static final List<String> BYPASS_PATTERNS = List.of(
            "/api/doctors/**",
            "/api/services/**",
            "/api/rooms/**",
            "/api/specializations/**",
            "/api/health-packages/**",
            "/api/hospitals/**",
            "/api/online-consultations/**");

    private PublicSecurityCatalog() {
    }

    /**
     * Phương thức: Bypass warnings for module.
     */
    public static List<String> bypassWarningsForModule(String canonicalPath) {
        String apiPath = canonicalPath.startsWith("/api") ? canonicalPath : "/api" + canonicalPath.replace("/**", "/**");
        return BYPASS_PATTERNS.stream()
                .filter(p -> pathsOverlap(p, apiPath))
                .map(p -> "GET " + p + " is public via SecurityConfig (guest/patient)")
                .toList();
    }

    /**
     * Phương thức: Paths overlap.
     */
    private static boolean pathsOverlap(String a, String b) {
        return MATCHER.match(a, b.replace("/**", "/probe"))
                || MATCHER.match(b, a.replace("/**", "/probe"))
                || a.replace("/**", "").equals(b.replace("/**", "").replace("/api", ""));
    }
}
