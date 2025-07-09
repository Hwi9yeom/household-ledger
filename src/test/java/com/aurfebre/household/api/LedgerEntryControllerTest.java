package com.aurfebre.household.api;

import com.aurfebre.household.domain.LedgerEntry;
import com.aurfebre.household.domain.enums.EntryType;
import com.aurfebre.household.domain.enums.TransactionType;
import com.aurfebre.household.service.LedgerEntryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LedgerEntryController.class)
@Import(com.aurfebre.household.config.SecurityConfig.class)
class LedgerEntryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LedgerEntryService ledgerEntryService;

    @Autowired
    private ObjectMapper objectMapper;

    private LedgerEntry testEntry;
    private List<LedgerEntry> testEntries;

    @BeforeEach
    void setUp() {
        testEntry = new LedgerEntry(
            1L,
            EntryType.INCOME,
            TransactionType.FIXED,
            1L,
            new BigDecimal("3000000"),
            LocalDate.of(2025, 7, 1),
            "월급"
        );
        testEntry.setId(1L);
        testEntry.setMemo("정기 급여");
        testEntry.setCreatedAt(LocalDateTime.now());

        LedgerEntry expenseEntry = new LedgerEntry(
            1L,
            EntryType.EXPENSE,
            TransactionType.VARIABLE,
            2L,
            new BigDecimal("15000"),
            LocalDate.of(2025, 7, 1),
            "점심"
        );
        expenseEntry.setId(2L);

        testEntries = Arrays.asList(testEntry, expenseEntry);
    }

    @Test
    void getAllLedgerEntries_ShouldReturnAllEntries() throws Exception {
        when(ledgerEntryService.getAllEntries()).thenReturn(testEntries);

        mockMvc.perform(get("/api/ledger-entries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].entryType", is("INCOME")))
                .andExpect(jsonPath("$[0].amount", is(3000000)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].entryType", is("EXPENSE")));
    }

    @Test
    void getLedgerEntriesByUserId_ShouldReturnUserEntries() throws Exception {
        when(ledgerEntryService.getEntriesByUserId(1L)).thenReturn(testEntries);

        mockMvc.perform(get("/api/ledger-entries/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId", is(1)))
                .andExpect(jsonPath("$[1].userId", is(1)));
    }

    @Test
    void getLedgerEntriesByUserIdAndType_ShouldReturnFilteredEntries() throws Exception {
        List<LedgerEntry> incomeEntries = Arrays.asList(testEntry);
        when(ledgerEntryService.getEntriesByUserIdAndType(1L, EntryType.INCOME)).thenReturn(incomeEntries);

        mockMvc.perform(get("/api/ledger-entries/user/1/type/INCOME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].entryType", is("INCOME")));
    }

    @Test
    void getLedgerEntriesByUserIdAndCategory_ShouldReturnCategoryEntries() throws Exception {
        List<LedgerEntry> categoryEntries = Arrays.asList(testEntry);
        when(ledgerEntryService.getEntriesByUserIdAndCategory(1L, 1L)).thenReturn(categoryEntries);

        mockMvc.perform(get("/api/ledger-entries/user/1/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].categoryId", is(1)));
    }

    @Test
    void getLedgerEntriesByUserIdAndDateRange_ShouldReturnEntriesInRange() throws Exception {
        LocalDate startDate = LocalDate.of(2025, 7, 1);
        LocalDate endDate = LocalDate.of(2025, 7, 31);
        when(ledgerEntryService.getEntriesByUserIdAndDateRange(1L, startDate, endDate)).thenReturn(testEntries);

        mockMvc.perform(get("/api/ledger-entries/user/1/range")
                .param("startDate", "2025-07-01")
                .param("endDate", "2025-07-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getLedgerEntriesByUserIdAndTypeAndDateRange_ShouldReturnFilteredEntries() throws Exception {
        LocalDate startDate = LocalDate.of(2025, 7, 1);
        LocalDate endDate = LocalDate.of(2025, 7, 31);
        List<LedgerEntry> incomeEntries = Arrays.asList(testEntry);
        when(ledgerEntryService.getEntriesByUserIdAndTypeAndDateRange(1L, EntryType.INCOME, startDate, endDate)).thenReturn(incomeEntries);

        mockMvc.perform(get("/api/ledger-entries/user/1/type/INCOME/range")
                .param("startDate", "2025-07-01")
                .param("endDate", "2025-07-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].entryType", is("INCOME")));
    }

    @Test
    void getTotalByUserIdAndTypeAndDateRange_ShouldReturnTotal() throws Exception {
        LocalDate startDate = LocalDate.of(2025, 7, 1);
        LocalDate endDate = LocalDate.of(2025, 7, 31);
        BigDecimal total = new BigDecimal("3000000");
        when(ledgerEntryService.getTotalByUserIdAndTypeAndDateRange(1L, EntryType.INCOME, startDate, endDate))
                .thenReturn(total);

        mockMvc.perform(get("/api/ledger-entries/user/1/type/INCOME/total")
                .param("startDate", "2025-07-01")
                .param("endDate", "2025-07-31"))
                .andExpect(status().isOk())
                .andExpect(content().string("3000000"));
    }

    @Test
    void getTotalByUserIdAndCategoryAndDateRange_ShouldReturnCategoryTotal() throws Exception {
        LocalDate startDate = LocalDate.of(2025, 7, 1);
        LocalDate endDate = LocalDate.of(2025, 7, 31);
        BigDecimal total = new BigDecimal("3000000");
        when(ledgerEntryService.getTotalByUserIdAndCategoryAndDateRange(1L, 1L, startDate, endDate))
                .thenReturn(total);

        mockMvc.perform(get("/api/ledger-entries/user/1/category/1/total")
                .param("startDate", "2025-07-01")
                .param("endDate", "2025-07-31"))
                .andExpect(status().isOk())
                .andExpect(content().string("3000000"));
    }

    @Test
    void getLedgerEntryById_WhenEntryExists_ShouldReturnEntry() throws Exception {
        when(ledgerEntryService.getEntryById(1L)).thenReturn(Optional.of(testEntry));

        mockMvc.perform(get("/api/ledger-entries/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.entryType", is("INCOME")))
                .andExpect(jsonPath("$.amount", is(3000000)));
    }

    @Test
    void getLedgerEntryById_WhenEntryNotExists_ShouldReturnNotFound() throws Exception {
        when(ledgerEntryService.getEntryById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ledger-entries/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createLedgerEntry_WhenValidEntry_ShouldReturnCreated() throws Exception {
        LedgerEntry newEntry = new LedgerEntry(
            1L, EntryType.EXPENSE, TransactionType.VARIABLE, 2L,
            new BigDecimal("15000"), LocalDate.of(2025, 7, 1), "점심"
        );
        newEntry.setId(3L);

        when(ledgerEntryService.createEntry(any(LedgerEntry.class))).thenReturn(newEntry);

        mockMvc.perform(post("/api/ledger-entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEntry)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.entryType", is("EXPENSE")))
                .andExpect(jsonPath("$.amount", is(15000)));
    }


    @Test
    void updateLedgerEntry_WhenValidUpdate_ShouldReturnUpdatedEntry() throws Exception {
        LedgerEntry updatedEntry = new LedgerEntry(
            1L, EntryType.EXPENSE, TransactionType.VARIABLE, 2L,
            new BigDecimal("20000"), LocalDate.of(2025, 7, 1), "수정된 점심"
        );
        updatedEntry.setId(1L);

        when(ledgerEntryService.updateEntry(eq(1L), any(LedgerEntry.class))).thenReturn(updatedEntry);

        mockMvc.perform(put("/api/ledger-entries/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEntry)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.entryType", is("EXPENSE")))
                .andExpect(jsonPath("$.amount", is(20000)))
                .andExpect(jsonPath("$.description", is("수정된 점심")));
    }

    @Test
    void updateLedgerEntry_WhenEntryNotExists_ShouldReturnNotFound() throws Exception {
        when(ledgerEntryService.updateEntry(eq(999L), any(LedgerEntry.class)))
                .thenThrow(new IllegalArgumentException("LedgerEntry not found with id: 999"));

        mockMvc.perform(put("/api/ledger-entries/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEntry)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteLedgerEntry_WhenEntryExists_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/ledger-entries/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteLedgerEntry_WhenEntryNotExists_ShouldReturnNotFound() throws Exception {
        doThrow(new IllegalArgumentException("LedgerEntry not found with id: 999"))
                .when(ledgerEntryService).deleteEntry(999L);

        mockMvc.perform(delete("/api/ledger-entries/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getLedgerEntriesByUserIdAndDateRange_WhenInvalidDateFormat_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/ledger-entries/user/1/range")
                .param("startDate", "invalid-date")
                .param("endDate", "2025-07-31"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getLedgerEntriesByUserIdAndType_WhenInvalidEntryType_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/ledger-entries/user/1/type/INVALID_TYPE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_require_jwt_to_access_entries() throws Exception {
        mockMvc.perform(get("/api/ledger-entries"))
                .andExpect(status().isUnauthorized());
    }
}