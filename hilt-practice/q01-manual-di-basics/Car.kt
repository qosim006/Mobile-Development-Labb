interface Engine {
    fun start(): Boolean
}

class PetrolEngine : Engine {
    override fun start() = true
}

class HardCar {
    private val engine = PetrolEngine()
    fun drive() = if (engine.start()) "driving" else "engine failure"
}

class Car(private val engine: Engine) {
    fun drive() = if (engine.start()) "driving" else "engine failure"
}

class FakeEngine(private val works: Boolean) : Engine {
    override fun start() = works
}
