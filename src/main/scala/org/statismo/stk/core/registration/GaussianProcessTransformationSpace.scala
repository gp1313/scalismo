package org.statismo.stk.core
package registration

import TransformationSpace.ParameterVector

import scala.NotImplementedError
import breeze.linalg.DenseVector

import org.statismo.stk.core.statisticalmodel.{ GaussianProcess, LowRankGaussianProcess }
import org.statismo.stk.core.geometry._

case class GaussianProcessTransformationSpace1D(gp: LowRankGaussianProcess[OneD]) extends TransformationSpace[OneD] with DifferentiableTransforms[OneD] {
  override type T = GaussianProcessTransformation1D

  def parametersDimensionality = gp.rank

  def inverseTransform(p: ParameterVector) = None

  def identityTransformParameters = DenseVector.zeros[Float](parametersDimensionality)

  override def transformForParameters(p: ParameterVector) = new GaussianProcessTransformation1D(gp, p)

  def takeDerivativeWRTParameters(p: ParameterVector) = { x: Point[OneD] =>
    gp.jacobian(p)(x)
  }

}

// the actual kernel transform
case class GaussianProcessTransformation1D(gp: LowRankGaussianProcess[OneD], alpha: ParameterVector) extends ParametricTransformation[OneD] with CanDifferentiate[OneD] {

  val instance = gp.instance(alpha)
  val parameters = alpha
  def apply(x: Point[OneD]): Point[OneD] = {
    val newPointAsVector = instance(x)
    Point1D(x(0) + newPointAsVector(0))
  }
  def takeDerivative(x: Point[OneD]) = { throw new NotImplementedError("take derivative of kernel") }
}

case class GaussianProcessTransformationSpace2D(gp: LowRankGaussianProcess[TwoD]) extends TransformationSpace[TwoD] with DifferentiableTransforms[TwoD] {

  override type T = GaussianProcessTransformation2D

  def identityTransformParameters = DenseVector.zeros[Float](parametersDimensionality)

  def parametersDimensionality = gp.rank

  def inverseTransform(p: ParameterVector) = None

  override def transformForParameters(p: ParameterVector) = new GaussianProcessTransformation2D(gp, p)

  def takeDerivativeWRTParameters(p: ParameterVector) = { x: Point[TwoD] =>
    gp.jacobian(p)(x)
  }
}

// the actual kernel transform
case class GaussianProcessTransformation2D(gp: LowRankGaussianProcess[TwoD], alpha: ParameterVector) extends ParametricTransformation[TwoD] with CanDifferentiate[TwoD] {

  val instance = gp.instance(alpha)
  val parameters = alpha
  def apply(x: Point[TwoD]): Point[TwoD] = {
    val newPointAsVector = instance(x)
    Point2D(x(0) + newPointAsVector(0), x(1) + newPointAsVector(1))
  }
  def takeDerivative(x: Point[TwoD]) = { throw new NotImplementedError("take derivative of kernel") }
}

case class GaussianProcessTransformationSpace3D(gp: LowRankGaussianProcess[ThreeD]) extends TransformationSpace[ThreeD] with DifferentiableTransforms[ThreeD] {

  override type T = GaussianProcessTransformation3D

  def identityTransformParameters = DenseVector.zeros[Float](parametersDimensionality)

  def parametersDimensionality = gp.rank

  def inverseTransform(p: ParameterVector) = None

  def transformForParameters(p: ParameterVector) = new GaussianProcessTransformation3D(gp, p)
  def takeDerivativeWRTParameters(p: ParameterVector) = { x: Point[ThreeD] =>
    gp.jacobian(p)(x)
  }

}

// the actual kernel transform
case class GaussianProcessTransformation3D(gp: LowRankGaussianProcess[ThreeD], alpha: ParameterVector) extends ParametricTransformation[ThreeD] with CanDifferentiate[ThreeD] {

  val instance = gp.instance(alpha)
  val parameters = alpha
  def apply(x: Point[ThreeD]): Point[ThreeD] = {
    val newPointAsVector = instance(x)
    Point3D(x(0) + newPointAsVector(0), x(1) + newPointAsVector(1), x(2) + newPointAsVector(2))
  }
  def takeDerivative(x: Point[ThreeD]) = { throw new NotImplementedError("take derivative of kernel") }
}

object GaussianProcessTransformationSpace {

}
