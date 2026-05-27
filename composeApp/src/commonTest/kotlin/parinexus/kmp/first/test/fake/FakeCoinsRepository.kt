package parinexus.kmp.first.test.fake

import parinexus.kmp.first.coins.data.repository.CoinsRepositoryImpl
import parinexus.kmp.first.coins.domain.api.CoinsRemoteDataSource
import parinexus.kmp.first.coins.domain.repository.CoinsRepository

fun FakeCoinsRepository(
    remote: FakeCoinsRemoteDataSource = FakeCoinsRemoteDataSource(),
    local: FakeMarketLocalDataSource = FakeMarketLocalDataSource(),
): CoinsRepository = CoinsRepositoryImpl(
    remote = remote,
    local = local,
)
