package smptk
package registration

import org.scalatest.FunSpec
import java.nio.ByteBuffer
import java.io.File
import java.io.IOException
import smptk.image.DiscreteImageDomain2D
import smptk.image.Geometry.CoordVector2D
import breeze.linalg.DenseVector
import org.scalatest.matchers.ShouldMatchers
import smptk.image.Geometry._
import smptk.image.Geometry.implicits._
import breeze.plot.Figure
import breeze.plot._
import smptk.io.ImageIO
import smptk.image.Interpolation
import smptk.image.Image._
import smptk.image.DiscreteImageDomain1D
import smptk.image.DiscreteScalarImage1D
import smptk.numerics.GradientDescentOptimizer
import smptk.numerics.GradientDescentConfiguration

class ImageTest extends FunSpec with ShouldMatchers {
  describe("A 2D rigid landmark based registration") {
    ignore("can retrieve correct parameters") {
      val points = IndexedSeq(CoordVector2D(0., 0.), CoordVector2D(1., 4.), CoordVector2D(2., 0.))

      val c = CoordVector2D(1., 4 / 3.)
      for (angle <- (1 until 16).map(i => math.Pi / i)) {
        val rotationParams = DenseVector(-angle)
        val transParams = DenseVector[Double](1f, 1.5f)
        val productParams = DenseVector.vertcat(transParams, rotationParams)

        val productSpace = RigidTransformationSpace2D(c)

        val transformedPoints = points.map((pt: CoordVector2D[Double]) => productSpace(productParams)(pt))

        val regResult = LandmarkRegistration.rigid2DLandmarkRegistration(points.zip(transformedPoints), c)

        val alignedPoints = points.map((pt: CoordVector2D[Double]) => regResult.transform(pt))

        (transformedPoints(0)(0) should be(alignedPoints(0)(0) plusOrMinus 0.0001))
        (transformedPoints(0)(1) should be(alignedPoints(0)(1) plusOrMinus 0.0001))
        (transformedPoints(1)(0) should be(alignedPoints(1)(0) plusOrMinus 0.0001))
        (transformedPoints(1)(1) should be(alignedPoints(1)(1) plusOrMinus 0.0001))
        (transformedPoints(2)(0) should be(alignedPoints(2)(0) plusOrMinus 0.0001))
        (transformedPoints(2)(1) should be(alignedPoints(2)(1) plusOrMinus 0.0001))
      }
    }
  }
  
  
  describe("A 2D image registration") {
    it("Recovers the correct parameters for a translation transfrom") {
      val testImgUrl = getClass().getResource("/dm128.h5").getPath()
      val discreteFixedImage = ImageIO.read2DScalarImage[Float](new File(testImgUrl)).get
      val fixedImage = Interpolation.interpolate2D(3)(discreteFixedImage)
      
      val domain = discreteFixedImage.domain
      val center = CoordVector2D(domain.origin(0) + domain.extent(0) / 2, domain.origin(1) + domain.extent(1) / 2)
 
    val regConf = RegistrationConfiguration[CoordVector2D] (
        optimizer = GradientDescentOptimizer(GradientDescentConfiguration(100, 0.001)),
        metric = MeanSquaresMetric2D(MeanSquaresMetricConfiguration()),
        transformationSpace = TranslationSpace2D(),
        regularizer = RKHSNormRegularizer,
        regularizationWeight = 0.0
      )
   
      
     // val rigidTransform = RigidTransformationSpace2D(center)(DenseVector(-0f,-0f, 3.14f  / 20))
      val translationTransform = regConf.transformationSpace(DenseVector(-25f, 25f))
      //val rotationTransform = RotationSpace2D(center)(DenseVector(3.14/20))
      val transformedLena =fixedImage compose translationTransform
      
      val registration = Registration.registration2D(regConf)(fixedImage, transformedLena)
      
      val regResult = registration(domain)    
      
     (regResult.parameters(0) should be (20. plusOrMinus 0.0001))
      (regResult.parameters(1) should be (-20. plusOrMinus 0.0001))
      //(regResult.parameters(0) should be (-3.14/20 plusOrMinus 0.0001))
      
    }
  }
  
    it("delete me") {
      val domain = DiscreteImageDomain1D(0., 1, 1000)
     
      val fixedImg = Interpolation.interpolate1D(3)(DiscreteScalarImage1D(domain, domain.points.map(x => x(0))))

      val regConf = RegistrationConfiguration[CoordVector1D] (
        optimizer = GradientDescentOptimizer(GradientDescentConfiguration(100, 0.001)),
        metric = MeanSquaresMetric1D(MeanSquaresMetricConfiguration()),
        transformationSpace = TranslationSpace1D(),
        regularizer = RKHSNormRegularizer,
        regularizationWeight = 0.0
      )
   

      val t = regConf.transformationSpace(DenseVector(5.))

      val warpedImage = fixedImg.warp(t, domain.isInside)

      val registration = Registration.registration1D(regConf)(fixedImg, warpedImage)

      val regResult = registration(domain)

    }
  
}
