package parinexus.kmp.first.portfolio.data

import androidx.sqlite.SQLiteException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import parinexus.kmp.first.coins.domain.api.CoinsRemoteDataSource
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.EmptyResult
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.domain.onError
import parinexus.kmp.first.core.domain.onSuccess
import parinexus.kmp.first.portfolio.data.local.PortfolioDao
import parinexus.kmp.first.portfolio.data.local.UserBalanceDao
import parinexus.kmp.first.portfolio.data.local.UserBalanceEntity
import parinexus.kmp.first.portfolio.data.mapper.toPortfolioCoinEntity
import parinexus.kmp.first.portfolio.data.mapper.toPortfolioCoinModel
import parinexus.kmp.first.portfolio.domain.PortfolioCoinModel
import parinexus.kmp.first.portfolio.domain.PortfolioRepository
import kotlin.collections.emptyList

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioRepositoryImpl(
    val portfolioDao: PortfolioDao,
    val userBalanceDao: UserBalanceDao,
    val coinsRemoteDataSource: CoinsRemoteDataSource
) : PortfolioRepository {
    override suspend fun initUserBalance() {
        val currentBalance = userBalanceDao.getCashBalance()
        if (currentBalance == null) {
            userBalanceDao.insertBalance(UserBalanceEntity(cashBalance = 10000.0))
        }
    }

    override fun getOwnedCoins(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>> {
        return portfolioDao.getAllOwnedCoins().flatMapLatest { portfolioCoinEntities ->
            if (portfolioCoinEntities.isEmpty()) {
                flow {
                    emit(Result.Success(emptyList()))
                }
            } else {
                flow {
                    coinsRemoteDataSource.getListOfCoins()
                        .onError { error ->
                            emit(Result.Error(error))
                        }
                        .onSuccess { coinsResponseDto ->
                            val ownedCoins =
                                portfolioCoinEntities.mapNotNull { portfolioCoinEntity ->
                                    val coin =
                                        coinsResponseDto.data.coins.find { it.uuid == portfolioCoinEntity.coinId }
                                    coin?.let {
                                        portfolioCoinEntity.toPortfolioCoinModel(it.price)
                                    }
                                }
                            emit(Result.Success(ownedCoins))
                        }
                }
            }
        }.catch {
            emit(Result.Error(error = DataError.Remote.UNKNOWN))
        }
    }

    override suspend fun getPortfolioCoinById(coinId: String): Result<PortfolioCoinModel?, DataError.Remote> {
        coinsRemoteDataSource.getCoinById(coinId)
            .onError { error ->
                return Result.Error(error)
            }
            .onSuccess { coinDetailsResponseDto ->
                val portfolioCoinEntity = portfolioDao.getCoinById(coinId)
                return if (portfolioCoinEntity != null) {
                    Result.Success(portfolioCoinEntity.toPortfolioCoinModel(coinDetailsResponseDto.data.coin.price))

                } else {
                    Result.Success(null)
                }

            }
        return Result.Error(DataError.Remote.UNKNOWN)
    }

    override suspend fun insertPortfolioCoin(portfolioCoinModel: PortfolioCoinModel): EmptyResult<DataError.Local> {
        try {
            portfolioDao.insert(portfolioCoinModel.toPortfolioCoinEntity())
            return Result.Success(Unit)

        } catch (e: SQLiteException) {
            return Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun removePortfolioCoin(coinId: String) {
        portfolioDao.deletePortfolioItem(coinId)
    }

    override fun calculatePortfolioValue(): Flow<Result<Double, DataError.Remote>> {
        return portfolioDao.getAllOwnedCoins().flatMapLatest { portfolioCoinEntities ->
            if (portfolioCoinEntities.isEmpty()) {
                flow {
                    emit(Result.Success(0.0))
                }

            } else {
                flow {
                    val apiResult = coinsRemoteDataSource.getListOfCoins()
                    apiResult.onError { error ->
                        emit(Result.Error(error))
                    }.onSuccess {
                        val totalValue = portfolioCoinEntities.sumOf { portfolioCoinEntity ->
                            val coinPrice =
                                it.data.coins.find { it.uuid == portfolioCoinEntity.coinId }?.price
                                    ?: 0.0
                            portfolioCoinEntity.amountOwned * coinPrice
                        }
                        emit(Result.Success(totalValue))
                    }
                }
            }.catch {
                emit(Result.Error(error = DataError.Remote.UNKNOWN))
            }
        }
    }

    override fun totalBalanceFlow(): Flow<Result<Double, DataError.Remote>> {
        return combine(
            totalCashBalanceFlow(),
            calculatePortfolioValue()
        ) { cashBalance, portfolioValue ->
            when (portfolioValue) {
                is Result.Success -> {
                    Result.Success(cashBalance + portfolioValue.data)
                }

                is Result.Error -> {
                    Result.Error(portfolioValue.error)
                }
            }
        }
    }

    override fun totalCashBalanceFlow(): Flow<Double> {
        return flow {
            emit(userBalanceDao.getCashBalance() ?: 10000.0)
        }
    }

    override suspend fun updateCashBalance(newBalance: Double) {
        userBalanceDao.updateCashBalance(newBalance)
    }
}