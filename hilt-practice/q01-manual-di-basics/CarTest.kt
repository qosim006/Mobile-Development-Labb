import org.junit.Assert.assertEquals
import org.junit.Test

class CarTest {

    @Test
    fun hardCar_alwaysUsesRealEngine() {
        assertEquals("driving", HardCar().drive())
    }

    @Test
    fun car_withBrokenFakeEngine() {
        assertEquals("engine failure", Car(FakeEngine(false)).drive())
    }

    @Test
    fun car_withWorkingFakeEngine() {
        assertEquals("driving", Car(FakeEngine(true)).drive())
    }
}
