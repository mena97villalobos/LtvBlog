package com.mena97villalobos.ltvblog.data.usecases

interface SingleUseCase<R> {
    suspend fun execute(): R
}
