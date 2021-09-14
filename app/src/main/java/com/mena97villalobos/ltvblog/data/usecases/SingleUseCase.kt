package com.mena97villalobos.ltvblog.data.usecases

interface SingleUseCase<R> {
    fun execute(): R
}
