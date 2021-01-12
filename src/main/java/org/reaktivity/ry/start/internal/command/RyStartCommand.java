/**
 * Copyright 2016-2020 The Reaktivity Project
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
package org.reaktivity.ry.start.internal.command;

import static org.agrona.LangUtil.rethrowUnchecked;
import static org.reaktivity.reaktor.ReaktorConfiguration.REAKTOR_DIRECTORY;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.reaktivity.reaktor.Reaktor;
import org.reaktivity.reaktor.ReaktorConfiguration;
import org.reaktivity.ry.RyCommand;

import com.github.rvesse.airline.annotations.Command;

@Command(name = "start")
public final class RyStartCommand extends RyCommand
{
    private final CountDownLatch latch = new CountDownLatch(1);
    private final Collection<Throwable> errors = new LinkedHashSet<>();

    @Override
    public void run()
    {
        Properties props = new Properties();
        props.setProperty(REAKTOR_DIRECTORY.name(), ".ry/engine");
        ReaktorConfiguration config = new ReaktorConfiguration(props);

        try (Reaktor reaktor = Reaktor.builder()
            .config(config)
            .threads(1)
            .errorHandler(this::onError)
            .build())
        {
            reaktor.start();
            System.out.println("started reaktor");

            Runtime.getRuntime().addShutdownHook(new Thread(latch::countDown));

            latch.await();

            System.out.println("stopping reaktor");
        }
        catch (Exception ex)
        {
            rethrowUnchecked(ex);
        }
    }

    private void onError(
        Throwable error)
    {
        errors.add(error);
        latch.countDown();
    }
}
