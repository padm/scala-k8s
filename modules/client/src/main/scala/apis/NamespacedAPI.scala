/*
 * Copyright 2022 Hossein Naderi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.hnaderi.k8s.client

import dev.hnaderi.k8s.utils.Decoder
import dev.hnaderi.k8s.utils.Encoder

trait NamespacedAPI {
  protected def namespace: String
}

abstract class APIGroupAPI(base: String) {
  case object resources extends APIResourceListingRequest(base)

  abstract class ResourceAPIBase[
      RES: Decoder,
      COL: Decoder
  ](resourceName: String) {
    protected val clusterwideUrl = s"$base/$resourceName"

    case class ListAll() extends ListingRequest[RES, COL](clusterwideUrl)
    trait ClusterwideAPIBuilders {
      val list: ListAll = ListAll()
    }
  }

  abstract class NamespacedResourceAPI[
      RES: Decoder: Encoder,
      COL: Decoder
  ](resourceName: String)
      extends ResourceAPIBase[RES, COL](resourceName) {
    protected def urlFor(namespace: String, name: String) =
      s"${baseUrlIn(namespace)}/$name"
    protected def baseUrlIn(namespace: String) =
      s"$base/namespaces/${namespace}/$resourceName"

    case class ListInNamespace(namespace: String)
        extends ListingRequest[RES, COL](baseUrlIn(namespace))
    case class Create(namespace: String, configmap: RES)
        extends CreateRequest(baseUrlIn(namespace), configmap)
    case class Get(namespace: String, name: String)
        extends GetRequest[RES](urlFor(namespace, name))
    case class Delete(namespace: String, name: String)
        extends DeleteRequest[RES](urlFor(namespace, name))

    trait NamespacedAPIBuilders extends NamespacedAPI {
      def get(name: String): Get = Get(namespace, name)
      val list: ListInNamespace = ListInNamespace(namespace)
      def delete(name: String): Delete = Delete(namespace, name)
    }
  }

  abstract class ClusterResourceAPI[
      RES: Decoder,
      COL: Decoder
  ](resourceName: String)
      extends ResourceAPIBase[RES, COL](resourceName) {
    protected def urlFor(name: String) =
      s"$clusterwideUrl/$name"

    case class Get(name: String) extends GetRequest[RES](urlFor(name))

    def get(name: String): Get = Get(name)
    val list: ListAll = ListAll()
  }

}