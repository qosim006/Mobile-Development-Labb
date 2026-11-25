package com.example.hiltpractice.experiments

import javax.inject.Inject

// Q9: creation order should print C, B, A
class C @Inject constructor() {
    init { println("created C") }
}

class B @Inject constructor(c: C) {
    init { println("created B") }
}

class A @Inject constructor(b: B) {
    init { println("created A") }
}
