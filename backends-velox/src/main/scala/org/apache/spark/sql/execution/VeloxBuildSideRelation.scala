/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql.execution

import io.glutenproject.execution.BroadCastHashJoinContext
import io.glutenproject.expression.ArrowConverterUtils

import org.apache.spark.sql.catalyst.expressions.{Attribute, Expression}
import org.apache.spark.sql.catalyst.plans.physical.BroadcastMode
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.execution.joins.BuildSideRelation
import org.apache.spark.sql.vectorized.ColumnarBatch

case class VeloxBuildSideRelation(mode: BroadcastMode,
                                  output: Seq[Attribute],
                                  batches: Array[Array[Byte]])
  extends BuildSideRelation {

  override def deserialized: Iterator[ColumnarBatch] = {
    ArrowConverterUtils.convertFromNetty(output, batches)
  }

  override def asReadOnlyCopy(broadCastContext: BroadCastHashJoinContext
                             ): VeloxBuildSideRelation = this

  /**
   * Transform columnar broadcasted value to Array[InternalRow] by key and distinct.
   * @return
   */
  override def transform(key: Expression): Array[InternalRow] = {
    // convert batches: Array[Array[Byte]] to Array[InternalRow] by key and distinct.
    Array.empty
  }
}
