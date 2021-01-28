/**
 * Copyright 2016-2021 The Reaktivity Project
 *
 * The Reaktivity Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
module org.reaktivity.ry.list
{
    requires org.reaktivity.ry;

    requires org.agrona.core;
    requires org.reaktivity.reaktor;

    opens org.reaktivity.ry.start.internal.command
       to com.github.rvesse.airline;

    provides org.reaktivity.ry.RyCommandSpi
        with org.reaktivity.ry.start.internal.RyStartCommandSpi;
}
