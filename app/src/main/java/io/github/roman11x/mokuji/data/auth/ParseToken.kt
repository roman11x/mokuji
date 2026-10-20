package io.github.roman11x.mokuji.data.auth

/**
 * Parses the AniList access token from a URL fragment string.
 *
 * @param fragment The URI fragment containing authentication parameters (e.g., "access_token=xyz&token_type=Bearer").
 * @return The extracted access token string, or null if the fragment is null or the token is missing/blank.
 */
fun parseToken(fragment: String?): String? {
    if (fragment == null) return null

    // Expected fragment shape: access_token=<token>&token_type=<type>&expires_in=<number>
    val token = fragment.split("&")
        .firstOrNull { it.startsWith("access_token=") }
        ?.substringAfter("access_token=")
        ?.takeIf { it.isNotBlank() } // Ensure we don't return an empty string as a valid token

    return token
}