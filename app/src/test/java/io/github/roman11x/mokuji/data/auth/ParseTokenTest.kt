package io.github.roman11x.mokuji.data.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ParseTokenTest {
    @Test
    fun `correct fragment shape returns token`() {
        val fragment = "access_token=abc&token_type=<type>&expires_in=<number>"
        val token = parseToken(fragment)
        assertEquals("abc", token)
    }

    @Test
    fun `pairs in another order in fragment return token`() {
        val fragment = "token_type=<type>&access_token=abc&expires_in=<number>"
        val token = parseToken(fragment)
        assertEquals("abc", token)
    }

    @Test
    fun `no  access_token pair returns null`() {
        val fragment = "token_type=<type>&expires_in=<number>"
        val token = parseToken(fragment)
        assertNull(token)
    }

    @Test
    fun `empty access_token returns null`() {
        val fragment = "access_token=&token_type=<type>&expires_in=<number>"
        val token = parseToken(fragment)
        assertNull(token)
    }

    @Test
    fun `empty fragment returns null`() {
        val fragment = ""
        val token = parseToken(fragment)
        assertNull(token)
    }

    @Test
    fun `null fragment returns null`() {
        val fragment = null
        val token = parseToken(fragment)
        assertNull(token)
    }

}

