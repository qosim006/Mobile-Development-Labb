# NOTES.md - Dependency Injection with Hilt (Practice 15)

How to read this file:
- **Where** tells you which file in this repo holds the code for the question.
- Lines marked **TODO (your run)** are intentionally empty: they need what you see on your own
  machine (error text, counts, screenshots). Fill them in after running.
- Theoretical answers are written out below. Check them against Lecture 22 and reword them.

---

## Section 1 - DI Fundamentals & Manual DI

### Q1 (hands-on) - HardCar vs Car
Where: `q01-manual-di-basics/Car.kt`, `CarTest.kt`
- Key points for your two sentences: what `HardCar` can be tested with / what it cannot; what `Car` can be tested with.
- TODO (your run): your two sentences.

### Q2 (theoretical) - construct / grab / supplied
| Snippet | Kind | Why |
|---|---|---|
| (a) `Room.databaseBuilder(...).build()` inside a class | construct | the class builds its own dependency |
| (b) `val api = ApiInstance.service` | grab | reaches into a global object |
| (c) `class Repo(private val api: Api)` | supplied | arrives through the constructor |
| (d) `val ctx = LocalContext.current` | grab | pulls the value from the ambient composition |
| (e) `by viewModels()` with a custom factory | supplied | the factory hands the ViewModel its dependencies |
| (f) `@Inject lateinit var logger: Logger` | supplied | the container fills the field from outside |

### Q3 (hands-on) - AppContainer with creation counters
Where: `extras/manual-di/AppContainer.kt.txt` (copy to your `manual-di` branch)
- TODO (your run): how many times each object was created over three screens; which should exist only once.

### Q4 (hands-on) - rewire the Lecture 21 app by hand
Branch: `manual-di`
- TODO (your run): files touched = ___ , lines added = ___ (`git diff --shortstat main..manual-di`).

### Q5 (theoretical) - dependency graph and pains
Graph: see the diagram in `README.md` (draw the same kind of graph for your own Notes app).
Pains a graph like this shows:
1. **Factory boilerplate**: every ViewModel needs a factory that knows how to build its dependencies.
2. **Scope / lifetime management**: someone must decide by hand which objects are created once (`OkHttpClient`, database) and which per screen.
3. **Wiring order and duplication**: the container must create things in the right order, and each new dependency means editing several files.

Hilt removes entirely: factory boilerplate (`@HiltViewModel`) and the manual wiring order (Dagger works out the order).
Hilt only moves: scope decisions (they become annotations such as `@Singleton`) and the knowledge of how to build third-party classes (they move into `@Provides` functions in modules).

---

## Section 2 - Setting Up Hilt

### Q6 (hands-on) - Gradle setup and two deliberate mistakes
Where: `gradle/libs.versions.toml`, root `build.gradle.kts`, `app/build.gradle.kts`
- TODO (your run): error when the plugin is applied but the compiler is missing.
- TODO (your run): error when the compiler is present but the plugin is missing.

### Q7 (hands-on) - @HiltAndroidApp
Where: `App.kt`, `AndroidManifest.xml`
- TODO (your run): crash after removing `android:name=".App"`.
- TODO (your run): error after removing `@HiltAndroidApp` instead.
- TODO (your run): what code Hilt generated for the application (search the build folder for `Hilt_App`).

### Q8 (hands-on) - @AndroidEntryPoint and field injection
Where: `MainActivity.kt`, `core/Logger.kt` (here `Logger` is already an interface bound in Q11; for this question first inject a concrete `Logger` class with `@Inject constructor()`)
- TODO (your run): error with a `private` field.
- TODO (your run): `UninitializedPropertyAccessException` text after removing `@AndroidEntryPoint`.
- Key points for the explanation: who creates activities (the system, not Hilt); why Hilt cannot call a constructor there; why constructor injection is preferred elsewhere (immutable fields, no lateinit, easy to test).
- TODO (your run): your explanation.

### Q9 (hands-on) - injection chain
Where: `experiments/Chain.kt`, `experiments/Scopes.kt` (`ScopeActivityA` injects `A` twice)
- TODO (your run): creation order printed; are `a1` and `a2` the same object?

### Q10 (theoretical) - reading a Dagger error
- Dagger errors start with a tag like `[Dagger/MissingBinding]` that **names the type that cannot be provided** in the first line.
- Below it, lines of the form "X is injected at ... Y" show **where the request started** and walk up the chain; the last line is the entry point (activity, ViewModel, component).
- The path in the error is the dependency chain from the entry point down to the missing type.
- **Missing binding** (an interface, or a third-party class such as `Retrofit`): Dagger has no way to create it. Fix: `@Binds` for an interface, `@Provides` in a module for a class you do not own (or `@Inject constructor` if you own it).
- **Dependency cycle** (`[Dagger/DependencyCycle]`, A needs B needs A): Dagger prints the cycle. Fix: break it by redesigning (extract the shared part into a third class) or, as a stop-gap, inject `Provider<T>` / `Lazy<T>` on one edge.

---

## Section 3 - Modules, Bindings & Qualifiers

### Q11 (hands-on) - @Binds
Where: `di/LoggingModule.kt`, `core/Logger.kt`
- TODO (your run): switch `LogcatLogger` to `FileLogger` in that one line; did every consumer follow?

### Q12 (hands-on) - NetworkModule with @Provides @Singleton
Where: `di/NetworkModule.kt` (a `println` in every provider)
- TODO (your run): how many times Retrofit was built while you visited three screens.

### Q13 (hands-on) - DatabaseModule and @ApplicationContext
Where: `di/DatabaseModule.kt`
- TODO (your run): error after removing `@ApplicationContext`.
- Key points: Hilt can provide several kinds of `Context` (application, activity); a plain `Context` is ambiguous about which one and about its lifetime.
- TODO (your run): your explanation.

### Q14 (hands-on) - two OkHttpClients, @Named then custom qualifiers
Where: `di/Qualifiers.kt` (`@ApiClient`, `@DownloadClient`), `di/NetworkModule.kt`
- First do it with `@Named("api")` / `@Named("download")`, then switch to the custom qualifiers (already in the code).
- TODO (your run): error when a qualifier is dropped at one injection site.
- Key points for "custom qualifiers beat strings": the compiler checks the annotation (a typo in a string only fails at build/runtime of the graph); you can rename and find usages with the IDE.
- TODO (your run): your two reasons.

### Q15 (hands-on) - @BaseUrl, @IoDispatcher and a JVM test
Where: `di/Qualifiers.kt`, `di/DispatcherModule.kt`, `data/UserRepository.kt`, `app/src/test/.../UserRepositoryTest.kt`
- TODO (your run): output of `./gradlew test` for that test class.

---

## Section 4 - Components & Scopes

### Q16 (hands-on) - scope experiment
Where: `experiments/Scopes.kt` (Notes screen -> "Scope experiment" button; read the `println` lines in Logcat)
Fill the prediction **before** running, then run and mark every wrong cell.

| Class | Activity A | Activity B | ViewModel | After rotation |
|---|---|---|---|---|
| Plain (no scope) | | | | |
| SingletonThing | | | | |
| RetainedThing | | | | |
| ViewModelThing | | | | |

- TODO (your run): the table above, plus which cells you got wrong.

### Q17 (theoretical) - component hierarchy
```
SingletonComponent
 |- ActivityRetainedComponent
 |   |- ViewModelComponent
 |   |- ActivityComponent
 |       |- FragmentComponent
 |           |- ViewComponent
 |- ServiceComponent
```
- A child can use everything its parent provides, not the other way around.
- `@Singleton` class needing an `@ActivityScoped` class: **illegal**. The parent cannot see a child's bindings (and would keep an activity alive forever).
- `@ActivityScoped` class needing a `@Singleton` class: **legal**. The child sees the parent's bindings.
- `@ViewModelScoped` class injected into an activity: **illegal**. `ViewModelComponent` is not an ancestor of `ActivityComponent`; it can only be injected into a ViewModel (or other `ViewModelComponent` bindings).
- Configuration change: `@Singleton`, `@ActivityRetainedScoped` and `@ViewModelScoped` objects survive; `@ActivityScoped` objects and the activity itself are recreated. Process death destroys all of them (only `SavedStateHandle` data is restored).

### Q18 (hands-on) - illegal hierarchies
Where: write the two bad classes yourself in a scratch file (do not commit the broken code on `main`).
- TODO (your run): error for `@Singleton` needing `@ActivityScoped`.
- TODO (your run): error for `@Singleton` needing `@ActivityContext`.
- Key point: a long-lived object holding an activity leaks it.
- TODO (your run): your two sentences.

### Q19 (hands-on) - multi-module split
Not generated in this repo (the module split is a structural change you make in Android Studio on your own branch).
Plan: `:data` (database, entities, `DatabaseModule`), `:feature-notes` (screen + ViewModel), `:app` (`@HiltAndroidApp`, depends on both). Apply the Hilt + KSP plugins in every module that has Hilt annotations.
- TODO (your run): what fails and why after removing the `:data` dependency from `:app`.

### Q20 (theoretical) - choosing scopes
| Class | Scope | Reason |
|---|---|---|
| `UserSession` (one per launch) | `@Singleton` | one shared instance for the whole process; must be thread-safe |
| `NoteListState` (one per screen visit) | `@ViewModelScoped` (or created inside the ViewModel) | tied to one screen's lifetime, survives rotation, must not outlive the screen |
| `HttpClient` (expensive) | `@Singleton` | costly to build, thread-safe, owns a connection pool that should be shared |
| `DateFormatter` (stateless, cheap) | none | cheap to create; a scope adds a lifetime to manage for no gain (and some formatters are not thread-safe) |
| `AuthToken` (changes at login) | not a scoped value; keep it in a `@Singleton` holder (for example a `TokenStore` with a `StateFlow`) | a scoped token instance would go stale at login/logout |

`@Singleton` is wrong for anything that is per-screen or per-user-session state, anything holding an activity or view context, and anything whose value changes during the app's life. "Just make it a singleton" skips the real decisions (how long should it live, who may change it, is it thread-safe) and creates hidden global state that is hard to test.

---

## Section 5 - Hilt in Android

### Q21 (hands-on) - migrate the app to Hilt
Branch: `hilt` (this repo is the result)
- TODO (your run): line counts of `manual-di` vs `hilt` branches (`git diff --shortstat`).

### Q22 (hands-on) - SavedStateHandle in a HiltViewModel
Where: `ui/ViewModels.kt` (`NoteDetailViewModel`), `ui/AppNav.kt` (`note/{noteId}`), `app/src/test/.../NoteDetailViewModelTest.kt`
- TODO (your run): result with *Don't keep activities* (open a note, background the app, return).

### Q23 (hands-on) - real and fake repository
Where: `data/NoteRepository.kt`, `di/RepositoryModule.kt`
Run the fake mode with `./gradlew installDebug -PuseFake=true`, the real mode without the flag.
- TODO (your run): screenshot of each mode.

### Q24 (hands-on) - injection into a BroadcastReceiver
Where: `receiver/BootReceiver.kt`
Trigger it with: `adb shell am broadcast -a android.intent.action.BOOT_COMPLETED -p com.example.hiltpractice`
- TODO (your run): log with `super.onReceive(...)` first.
- TODO (your run): what goes wrong without the `super` call.
- Key point: the receiver is created by the system, so Hilt can only inject inside `onReceive` (through the generated `Hilt_BootReceiver`).
- TODO (your run): your explanation.

### Q25 (hands-on) - shared ViewModel in a nested graph
Where: `ui/AppNav.kt` (graph `edit`), `ui/ViewModels.kt` (`DraftViewModel`)
- TODO (your run): draft visible on the second screen? Is `onCleared` logged when you leave the graph?

---

## Section 6 - Testing, Replacing Modules & Capstone

### Q26 (theoretical) - Hilt testing pieces
- `@TestInstallIn(components = [SingletonComponent::class], replaces = [NetworkModule::class])` removes the production module from the graph for the tests and installs the test module in its place.
- `@HiltAndroidTest` tells Hilt to generate a test-specific component for that test class.
- `HiltAndroidRule` creates and manages that component per test and must run first (`order = 0`); `hiltRule.inject()` fills the test's own `@Inject` fields.
- A custom `AndroidJUnitRunner` returning `HiltTestApplication` makes the instrumented test run with a Hilt-aware `Application` instead of the real one.
- A unit test of a class with an `@Inject constructor` needs none of this: the constructor is just a constructor, so the test passes fakes in by hand and no graph is built.

### Q27 (hands-on) - JVM tests without Hilt
Where: `app/src/test/...` (10 tests, hand-made fakes only). Run: `./gradlew test`
- TODO (your run): time taken, and which of your classes you could test without Hilt and why.

### Q28 (hands-on) - instrumented test setup
Where: `app/build.gradle.kts`, `androidTest/.../HiltTestRunner.kt`, `HiltSmokeTest.kt`
- TODO (your run): failure after removing the runner.
- TODO (your run): failure after removing the rule.
- TODO (your run): failure after removing `inject()`.

### Q29 (hands-on) - replaced database module + Compose test
Where: `androidTest/.../TestModules.kt` (`TestDatabaseModule`), `NotesUiTest.kt`
- TODO (your run): proof that the second run starts with an empty list.

### Q30 (capstone)
See `README.md` (component and dependency diagram, test list, how to run).
