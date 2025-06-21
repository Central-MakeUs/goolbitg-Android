package com.project.data.remote.common

import com.google.gson.Gson
import com.project.data.remote.response.base.BaseErrRes
import com.project.domain.model.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

object NetworkUtils {
    fun <T : Any, R : Any> handleApi(
        execute: suspend () -> Response<T>,
        mapper: (T?) -> R?,
    ): Flow<DataState<R>> {
        return flow {
            emit(DataState.Loading(isLoading = true))
            try {
                val response = execute()
                // 성공적으로 수행했을 경우에는 OK, CREATED인지만 확인
                val stateRes =
                    if (response.isSuccessful && (response.code() == 200 || response.code() == 201)) {
                        val body = response.body()
                        DataState.Success(mapper(body))
                    } else {
                        // Bad Request일 경우에는 errorCode를 확인 후 Error를 던짐
                        val errorBody = response.errorBody()?.toBaseErrResponse()
                        DataState.Error(code = errorBody?.code ?: -1, message = errorBody?.msg ?: "")
                    }
                emit(stateRes)
            } catch (e: HttpException) {
                emit(DataState.Error(code = e.code(), message = e.message()))
            } catch (e: Exception) {
                emit(DataState.Exception(e))
            } finally {
                emit(DataState.Loading(isLoading = false))
            }
        }
    }

    /** errorBody에 대한 ResponseBody를 기존 BaseBody와 동일하게 변환*/
    private fun ResponseBody.toBaseErrResponse(): BaseErrRes? {
        return try {
            val gson = Gson()
            gson.fromJson(this.charStream(), BaseErrRes::class.java)
        } catch (e: Exception) {
            null // 변환 실패 시 null 반환
        }
    }
}
