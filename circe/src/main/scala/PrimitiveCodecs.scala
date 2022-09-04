package dev.hnaderi.k8s
package circe

import io.circe._
import io.circe.syntax._
import io.k8s.apiextensions_apiserver.pkg.apis.apiextensions.v1.JSONSchemaProps
import io.k8s.apiextensions_apiserver.pkg.apis.apiextensions.v1.JSONSchemaPropsOrArray
import io.k8s.apiextensions_apiserver.pkg.apis.apiextensions.v1.JSONSchemaPropsOrBool
import io.k8s.apiextensions_apiserver.pkg.apis.apiextensions.v1.JSONSchemaPropsOrStringArray
import io.k8s.apimachinery.pkg.api.resource.Quantity
import io.k8s.apimachinery.pkg.apis.meta.v1.MicroTime
import io.k8s.apimachinery.pkg.apis.meta.v1.Time
import io.k8s.apimachinery.pkg.util.intstr.IntOrString

import codecs.io_k8s_apiextensions_apiserver_pkg_apis_apiextensions_v1JSONSchemaProps

private[circe] object PrimitiveCodec {
  implicit val intOrStringEncoder: Encoder[IntOrString] = {
    case IntOrString.IntValue(i)    => i.asJson
    case IntOrString.StringValue(s) => s.asJson
  }
  implicit val intOrStringDecoder: Decoder[IntOrString] =
    Decoder[String].map(IntOrString(_)).or(Decoder[Int].map(IntOrString(_)))

  implicit val timeEncoder: Encoder[Time] = t => t.value.asJson
  implicit val timeDecoder: Decoder[Time] = Decoder[String].map(Time(_))

  implicit val microTimeEncoder: Encoder[MicroTime] = t => t.value.asJson
  implicit val microTimeDecoder: Decoder[MicroTime] =
    Decoder[String].map(MicroTime(_))

  implicit val quantityEncoder: Encoder[Quantity] = t => t.value.asJson
  implicit val quantityDecoder: Decoder[Quantity] =
    Decoder[String].map(Quantity(_))

  implicit val jsonSchemaPropsOrBoolEncoder: Encoder[JSONSchemaPropsOrBool] = {
    case JSONSchemaPropsOrBool.BoolValue(b)  => b.asJson
    case JSONSchemaPropsOrBool.PropsValue(p) => p.asJson
  }
  implicit val jsonSchemaPropsOrBoolDecoder: Decoder[JSONSchemaPropsOrBool] =
    Decoder[JSONSchemaProps]
      .map(JSONSchemaPropsOrBool(_))
      .or(Decoder[Boolean].map(JSONSchemaPropsOrBool(_)))

  implicit val jsonSchemaPropsOrArrayEncoder
      : Encoder[JSONSchemaPropsOrArray] = {
    case JSONSchemaPropsOrArray.SingleValue(p)    => p.asJson
    case JSONSchemaPropsOrArray.MutipleValues(ps) => ps.asJson
  }
  implicit val jsonSchemaPropsOrArrayDecoder: Decoder[JSONSchemaPropsOrArray] =
    Decoder[Seq[JSONSchemaProps]]
      .map(JSONSchemaPropsOrArray.MutipleValues)
      .or(Decoder[JSONSchemaProps].map(JSONSchemaPropsOrArray(_)))

  implicit val jsonSchemaPropsOrStringArrayEncoder
      : Encoder[JSONSchemaPropsOrStringArray] = {
    case JSONSchemaPropsOrStringArray.PropsValue(p)  => p.asJson
    case JSONSchemaPropsOrStringArray.StringList(ss) => ss.asJson
  }
  implicit val jsonSchemaPropsOrStringArrayDecoder
      : Decoder[JSONSchemaPropsOrStringArray] =
    Decoder[JSONSchemaProps]
      .map(JSONSchemaPropsOrStringArray(_))
      .or(Decoder[Seq[String]].map(JSONSchemaPropsOrStringArray(_)))
}
