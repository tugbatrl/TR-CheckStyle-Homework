///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2026 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
///////////////////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;

/**
 * Holds the settings that decide when an ANT build is considered failed
 * because of violations, and reports such a failure.
 */
// Code Smell Refactor
final class FailurePolicy {

    /** Whether to fail build on violations. */
    private boolean failOnViolation = true;

    /** Property to set on violations. */
    private String failureProperty;

    /** The maximum number of errors that are tolerated. */
    private int maxErrors;

    /** The maximum number of warnings that are tolerated. */
    private int maxWarnings = Integer.MAX_VALUE;

    /**
     * Sets flag - whether to fail if a violation is found.
     *
     * @param fail whether to fail if a violation is found
     */
    /* package */ void setFailOnViolation(boolean fail) {
        failOnViolation = fail;
    }

    /**
     * Sets the name of the property to set when there is a violation.
     *
     * @param propertyName the name of the property to set in the event of a failure.
     */
    /* package */ void setFailureProperty(String propertyName) {
        failureProperty = propertyName;
    }

    /**
     * Sets the maximum number of errors allowed.
     *
     * @param maxErrors the maximum number of errors allowed.
     */
    /* package */ void setMaxErrors(int maxErrors) {
        this.maxErrors = maxErrors;
    }

    /**
     * Sets the maximum number of warnings allowed.
     *
     * @param maxWarnings the maximum number of warnings allowed.
     */
    /* package */ void setMaxWarnings(int maxWarnings) {
        this.maxWarnings = maxWarnings;
    }

    /**
     * Checks the result of a run against the configured limits.
     * Sets the failure property and throws when the limits are exceeded
     * and failing on violations is enabled.
     *
     * @param project the project in which the failure property is set
     * @param location the location reported in the thrown exception
     * @param numErrs number of errors found
     * @param numWarnings number of warnings found
     * @throws BuildException if the limits are exceeded and build should fail.
     */
    /* package */ void check(Project project, Location location,
                             int numErrs, int numWarnings) {
        final boolean okStatus = numErrs <= maxErrors && numWarnings <= maxWarnings;

        // Handle the return status
        if (!okStatus) {
            final String failureMsg =
                    "Got " + numErrs + " errors (max allowed: " + maxErrors + ") and "
                            + numWarnings + " warnings.";
            if (failureProperty != null) {
                project.setProperty(failureProperty, failureMsg);
            }

            if (failOnViolation) {
                throw new BuildException(failureMsg, location);
            }
        }
    }

}