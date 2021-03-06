/*
 * Copyright 2016 Dmitry Korotych
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dkorotych.gradle.maven.exec

import org.gradle.process.internal.DefaultExecAction
import spock.lang.Unroll
import spock.util.environment.RestoreSystemProperties

/**
 * @author Dmitry Korotych (dkorotych at gmail dot com)
 */
class MavenExecPluginProjectConventionTest extends MavenExecSpecification {

    def setup() {
        registerMavenDescriptorMock()
    }

    def "mavenexec should generate UnsupportedOperationException with null configuration closure"() {
        when:
        MavenExecPluginProjectConvention convention = new MavenExecPluginProjectConvention(null)
        convention.mavenexec(null)

        then:
        def assertionError = thrown(AssertionError)
        assert assertionError.message.startsWith('configure closure should not be null')
    }

    @Unroll
    @RestoreSystemProperties
    def "mavenexec. #os.familyName, #goals"() {
        setup:
        setOperatingSystem(os)
        DefaultExecAction delegate = Mock(DefaultExecAction)
        MavenExecPluginProjectConvention convention = new MavenExecPluginProjectConvention(delegate)

        when:
        convention.mavenexec {
            setGoals(goals)
        }

        then:
        1 * delegate.setCommandLine(commandLine)
        1 * delegate.execute()
        0 * delegate._

        where:
        [goals, os, commandLine] << setGoalsDataProvider()
    }

    @RestoreSystemProperties
    def "mavenexec"() {
        setup:
        asWindows()
        DefaultExecAction delegate = Mock(DefaultExecAction)
        MavenExecPluginProjectConvention convention = new MavenExecPluginProjectConvention(delegate)

        when:
        convention.mavenexec {
            goals 'site'
            quiet = true
            threads = '2C'
            offline = true
        }

        then:
        1 * delegate.setCommandLine(['cmd', '/c', 'mvn.cmd', '--quiet', '--threads', '2C', '--offline', 'site'])
        1 * delegate.execute()
        0 * delegate._
    }
}
