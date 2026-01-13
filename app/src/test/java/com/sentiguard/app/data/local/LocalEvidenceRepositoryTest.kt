package com.sentiguard.app.data.local

import com.sentiguard.app.data.local.db.EvidenceDao
import com.sentiguard.app.data.local.db.SessionDao
import com.sentiguard.app.data.local.db.SessionEntity
import com.sentiguard.app.domain.model.Session
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import java.time.LocalDateTime

class LocalEvidenceRepositoryTest {

    @Mock
    lateinit var sessionDao: SessionDao

    @Mock
    lateinit var evidenceDao: EvidenceDao

    private lateinit var repository: LocalEvidenceRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = LocalEvidenceRepository(sessionDao, evidenceDao)
    }

    @Test
    fun startSession_createsNewSession() = runBlocking {
        // When
        val result = repository.startSession()

        // Then
        assertNotNull(result.getOrNull())
        
        argumentCaptor<SessionEntity>().apply {
            verify(sessionDao).insertSession(capture())
            assertEquals(true, firstValue.isActive)
            assertNotNull(firstValue.id)
        }
    }

    @Test
    fun getActiveSession_mapsEntityToDomain() = runBlocking {
        // Given
        val entity = SessionEntity("123", LocalDateTime.now(), null, true)
        `when`(sessionDao.getActiveSession()).thenReturn(flowOf(entity))

        // When
        val result = repository.getActiveSession().first()

        // Then
        assertEquals("123", result?.id)
        assertEquals(true, result?.isActive)
    }
}
