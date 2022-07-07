package fi.spectrumlabs.rates.resolver.models

import fi.spectrumlabs.core.AdaAssetClass
import fi.spectrumlabs.core.models.domain.{Amount, AssetAmount, AssetClass, Pool, PoolId}
import org.scalacheck._

object Models {

  def genPoolId: Gen[PoolId] = Gen.listOfN(16, Gen.alphaChar).map(_.mkString).map(PoolId(_))

  def genAssetClass: Gen[AssetClass] =
    for {
      tokenName      <- Gen.listOfN(16, Gen.alphaChar).map(_.mkString)
      currencySymbol <- Gen.listOfN(32, Gen.alphaChar).map(_.mkString)
    } yield AssetClass(currencySymbol, tokenName)

  def getAssetAmount: Gen[AssetAmount] =
    for {
      asset  <- genAssetClass
      amount <- Gen.choose(1000000, 1000000000000L)
    } yield AssetAmount(asset, Amount(amount))

  def genAdaPool: Gen[Pool] =
    for {
      asset     <- getAssetAmount
      poolId    <- genPoolId
      adaAmount <- Gen.choose(1000000, 1000000000000L)
    } yield
      Pool(
        poolId,
        asset,
        AssetAmount(AdaAssetClass, Amount(adaAmount))
      )

  def genPool: Gen[Pool] =
    for {
      assetX <- getAssetAmount
      assetY <- getAssetAmount
      poolId <- genPoolId
    } yield Pool(poolId, assetX, assetY)
}
