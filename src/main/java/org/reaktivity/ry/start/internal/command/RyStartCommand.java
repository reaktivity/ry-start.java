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
package org.reaktivity.ry.start.internal.command;

import static java.lang.Runtime.getRuntime;
import static org.agrona.LangUtil.rethrowUnchecked;
import static org.reaktivity.reaktor.ReaktorConfiguration.REAKTOR_DIRECTORY;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.reaktivity.reaktor.Reaktor;
import org.reaktivity.reaktor.ReaktorConfiguration;
import org.reaktivity.ry.RyCommand;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;

@Command(name = "start", description = "Start engine")
public final class RyStartCommand extends RyCommand
{
    private final CountDownLatch latch = new CountDownLatch(1);
    private final Collection<Throwable> errors = new LinkedHashSet<>();

    @Option(name = "-c", description = "config")
    public URI configURI = Paths.get("ry.json").toUri();

    @Option(name = "-w", description = "workers")
    public int workers = 1;

    @Override
    public void run()
    {
        Runtime runtime = getRuntime();
        Properties props = new Properties();
        props.setProperty(REAKTOR_DIRECTORY.name(), ".ry/engine");
        ReaktorConfiguration config = new ReaktorConfiguration(props);

        try (Reaktor reaktor = Reaktor.builder()
            .config(config)
            .configURL(configURI.toURL())
            .threads(workers)
            .errorHandler(this::onError)
            .build())
        {
            reaktor.start();

            System.out.println("started");

            runtime.addShutdownHook(new Thread(latch::countDown));

            latch.await();

            errors.forEach(e -> e.printStackTrace(System.err));

            System.out.println("stopped");
        }
        catch (Throwable ex)
        {
            System.out.println("error");
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
