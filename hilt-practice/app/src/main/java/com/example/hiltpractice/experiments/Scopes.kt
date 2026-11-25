package com.example.hiltpractice.experiments

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import javax.inject.Singleton

// Q16: four classes that count their constructions
class Plain @Inject constructor() {
    init { println("created Plain") }
}

@Singleton
class SingletonThing @Inject constructor() {
    init { println("created SingletonThing") }
}

@ActivityRetainedScoped
class RetainedThing @Inject constructor() {
    init { println("created RetainedThing") }
}

@ViewModelScoped
class ViewModelThing @Inject constructor() {
    init { println("created ViewModelThing") }
}

@HiltViewModel
class ScopeViewModel @Inject constructor(
    val plain: Plain,
    val singleton: SingletonThing,
    val retained: RetainedThing,
    val viewModelThing: ViewModelThing,
) : ViewModel()

@AndroidEntryPoint
class ScopeActivityA : ComponentActivity() {

    @Inject lateinit var plain: Plain
    @Inject lateinit var singleton: SingletonThing
    @Inject lateinit var retained: RetainedThing

    // Q16: try adding  @Inject lateinit var viewModelThing: ViewModelThing  here and see what happens

    // Q9: two fields of the same type
    @Inject lateinit var a1: A
    @Inject lateinit var a2: A

    private val vm: ScopeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("A: a1 === a2 is ${a1 === a2}")
        println("A: plain=${plain.hashCode()} singleton=${singleton.hashCode()} retained=${retained.hashCode()}")
        println("A: vm.plain=${vm.plain.hashCode()} vm.singleton=${vm.singleton.hashCode()} " +
            "vm.retained=${vm.retained.hashCode()} vm.viewModelThing=${vm.viewModelThing.hashCode()}")
        setContent {
            Column(Modifier.statusBarsPadding().padding(16.dp)) {
                Text("ScopeActivityA")
                Button(onClick = { startActivity(Intent(this@ScopeActivityA, ScopeActivityB::class.java)) }) {
                    Text("Open B")
                }
            }
        }
    }
}

@AndroidEntryPoint
class ScopeActivityB : ComponentActivity() {

    @Inject lateinit var plain: Plain
    @Inject lateinit var singleton: SingletonThing
    @Inject lateinit var retained: RetainedThing

    private val vm: ScopeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("B: plain=${plain.hashCode()} singleton=${singleton.hashCode()} retained=${retained.hashCode()}")
        println("B: vm.plain=${vm.plain.hashCode()} vm.singleton=${vm.singleton.hashCode()} " +
            "vm.retained=${vm.retained.hashCode()} vm.viewModelThing=${vm.viewModelThing.hashCode()}")
        setContent {
            Column(Modifier.statusBarsPadding().padding(16.dp)) {
                Text("ScopeActivityB")
                Button(onClick = { startActivity(Intent(this@ScopeActivityB, ScopeActivityA::class.java)) }) {
                    Text("Open A")
                }
            }
        }
    }
}
