package mn.edu.must.sqat;

// ЛАБ 11-ийн ЭХЛЭЛИЙН тестийн багц.
// АНХААР: Энэ багц JaCoCo-гоор Line 100% (Branch 38/41) өгдөг —
// гэхдээ assertion-ууд нь ЗОРИУДААР СУЛ бичигдсэн.
// Таны даалгавар: PIT-ээр амьд үлдсэн мутантуудыг олж,
// тестүүдийг ХҮЧТЭЙ болгож mutation score-ийг өсгөх.

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScholarshipCalculatorTest {

    ScholarshipCalculator calc;

    @BeforeEach
    void setUp() { calc = new ScholarshipCalculator(); }

    // ---- letterGrade: B1 — муж бүрт ЯГ ямар үсэг буцаахыг шалгана ----

    @Test
    void letterGrade_returnsExactGradeForAllRanges() {
        assertEquals("A", calc.letterGrade(95));
        assertEquals("B", calc.letterGrade(85));
        assertEquals("C", calc.letterGrade(75));
        assertEquals("D", calc.letterGrade(65));
        assertEquals("F", calc.letterGrade(30));
    }

    @Test
    void letterGrade_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(-1));
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(101));
    }

    // ---- gpa: B1 — мужийн оронд ЯГ голчийг шалгана ----

    @Test
    void gpa_returnsExactAverage() {
        assertEquals(3.5, calc.gpa(new double[]{95, 85}), 1e-9);  // (4 + 3) / 2
        assertEquals(1.5, calc.gpa(new double[]{75, 65}), 1e-9);  // (2 + 1) / 2
        assertEquals(0.0, calc.gpa(new double[]{40}), 1e-9);      // F = 0
    }

    @Test
    void gpa_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(null));
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(new double[]{}));
    }

    // ---- scholarship: B1 — дүрэм бүрийн ЯГ хувийг шалгана ----

    @Test
    void scholarship_returnsExactPercent() {
        assertEquals(100, calc.scholarship(3.9, 16, false));
        assertEquals(70,  calc.scholarship(3.5, 13, false));
        assertEquals(50,  calc.scholarship(3.1, 5, false));
        assertEquals(0,   calc.scholarship(2.0, 1, false));
        assertEquals(0,   calc.scholarship(4.0, 20, true));   // сахилгын зөрчил
    }

    @Test
    void scholarship_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(4.1, 10, false));
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(3.0, -1, false));
    }
}
