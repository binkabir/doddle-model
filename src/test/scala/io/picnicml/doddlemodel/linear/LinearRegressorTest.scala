package io.picnicml.doddlemodel.linear

import breeze.linalg.{DenseMatrix, DenseVector}
import io.picnicml.doddlemodel.data.{Features, RealVector, Target}
import io.picnicml.doddlemodel.linear.typeclasses.LinearRegressor
import org.scalatest.{FlatSpec, Matchers}

case class DummyLinearRegressor(w: Option[RealVector])

class LinearRegressorTest extends FlatSpec with Matchers {

  val ev: LinearRegressor[DummyLinearRegressor] = new LinearRegressor[DummyLinearRegressor] {

    override protected def w(model: DummyLinearRegressor): Option[RealVector] = model.w

    override protected def copy(model: DummyLinearRegressor): DummyLinearRegressor = model.copy()

    override protected def copy(model: DummyLinearRegressor, w: RealVector): DummyLinearRegressor =
      model.copy(Some(w))

    override protected def targetVariableAppropriate(y: Target): Boolean = true

    override protected def predictStateless(model: DummyLinearRegressor, w: RealVector, x: Features): Target =
      x * w

    override protected[linear] def lossStateless(model: DummyLinearRegressor,
                                                 w: RealVector, x: Features, y: Target): Double = 0

    override protected[linear] def lossGradStateless(model: DummyLinearRegressor,
                                                     w: RealVector, x: Features, y: Target): RealVector = w
  }

  private val x = DenseMatrix.rand[Double](10, 5)
  private val y = DenseVector.rand[Double](10)
  private val model = DummyLinearRegressor(None)

  "Linear regressor" should "throw an exception when using fit, predict on trained, untrained models" in {
    an [IllegalArgumentException] should be thrownBy ev.predict(model, x)
    val trainedModel = ev.fit(model, x, y)
    an [IllegalArgumentException] should be thrownBy ev.fit(trainedModel, x, y)
  }

  it should "implement predictor functions" in {
    ev.isFitted(model) shouldBe false
    val trainedModel = ev.fit(model, x, y)
    ev.isFitted(trainedModel) shouldBe true
    val yPred = ev.predict(trainedModel, x)
    yPred.length shouldEqual y.length
  }
}
