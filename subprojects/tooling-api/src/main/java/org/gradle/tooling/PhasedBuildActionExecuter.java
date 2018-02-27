/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.tooling;

import org.gradle.api.Incubating;
import org.gradle.tooling.exceptions.MultipleBuildActionsException;

/**
 * Used to execute multiple {@link BuildAction}s in different phases of the build process.
 *
 * @since 4.7
 */
@Incubating
public interface PhasedBuildActionExecuter extends ConfigurableLauncher<PhasedBuildActionExecuter> {

    /**
     * Builder for a {@link PhasedBuildActionExecuter}.
     *
     * <p>A single {@link BuildAction} is allowed per build phase. Use composite actions if needed.
     *
     * @since 4.7
     */
    @Incubating
    interface Builder {

        /**
         * Adds the given action and its result handler to be executed after projects are loaded.
         *
         * <p>The action or model builders invoked by it are run when plugins have not yet be applied, and so it should try to get only gradle default models.
         *
         * <p>If the operation fails, the handler's {@link ResultHandler#onFailure(GradleConnectionException)} method is called with the appropriate exception.
         *
         * @param buildAction The action to run in the specified build phase.
         * @param handler The handler to supply the result of the given action to.
         * @param <T> The returning type of the action.
         * @return The builder.
         * @throws MultipleBuildActionsException If an action has already been added to this build phase. Multiple actions per phase are not supported yet.
         *
         * @since 4.7
         */
        <T> Builder addAfterLoadingAction(BuildAction<T> buildAction, ResultHandler<? super T> handler) throws MultipleBuildActionsException;

        /**
         * Adds the given action and its result handler to be executed after projects are configured and before tasks are run.
         *
         * <p>The action has already access to plugins' model builders. These model builders can modify the task graph, since tasks have not yet been run.
         *
         * <p>If the operation fails, the handler's {@link ResultHandler#onFailure(GradleConnectionException)} method is called with the appropriate exception.
         *
         * @param buildAction The action to run in the specified build phase.
         * @param handler The handler to supply the result of the given action to.
         * @param <T> The returning type of the action.
         * @return The builder.
         * @throws MultipleBuildActionsException If an action has already been added to this build phase. Multiple actions per phase are not supported yet.
         *
         * @since 4.7
         */
        <T> Builder addAfterConfigurationAction(BuildAction<T> buildAction, ResultHandler<? super T> handler) throws MultipleBuildActionsException;

        /**
         * Adds the given action and its result handler to be executed after tasks are run.
         *
         * <p>The action should not invoke task graph modifiers, since tasks have already been run.
         *
         * <p>If the operation fails, the handler's {@link ResultHandler#onFailure(GradleConnectionException)} method is called with the appropriate exception.
         *
         * @param buildAction The action to run in the specified build phase.
         * @param handler The handler to supply the result of the given action to.
         * @param <T> The returning type of the action.
         * @return The builder.
         * @throws MultipleBuildActionsException If an action has already been added to this build phase. Multiple actions per phase are not supported yet.
         *
         * @since 4.7
         */
        <T> Builder addAfterBuildAction(BuildAction<T> buildAction, ResultHandler<? super T> handler) throws MultipleBuildActionsException;

        /**
         * Builds the executer from the added actions.
         *
         * @return The executer.
         *
         * @since 4.7
         */
        PhasedBuildActionExecuter build();
    }

    /**
     * Specifies the tasks to execute before executing the AfterBuildAction and after the AfterConfigurationAction.
     *
     * The graph task can be changed in model builders invoked. If not configured, null, or an empty array is passed, then no tasks will be executed unless one of the model builders configures it.
     *
     * @param tasks The paths of the tasks to be executed. Relative paths are evaluated relative to the project for which this launcher was created.
     * @return this
     * @since 4.7
     */
    @Incubating
    PhasedBuildActionExecuter forTasks(String... tasks);

    /**
     * Specifies the tasks to execute before executing the AfterBuildAction and after the AfterConfigurationAction.
     *
     * The graph task can be changed in model builders invoked. If not configured, null, or an empty array is passed, then no tasks will be executed unless one of the model builders configures it.
     *
     * @param tasks The paths of the tasks to be executed. Relative paths are evaluated relative to the project for which this launcher was created.
     * @return this
     * @since 4.7
     */
    @Incubating
    PhasedBuildActionExecuter forTasks(Iterable<String> tasks);

    /**
     * Runs all the actions in their respective build phases, blocking until build is finished.
     *
     * <p>Results of each action are sent to their respective result handlers. If one of the actions fails, the build is interrupted.
     *
     * @throws UnsupportedVersionException When the target Gradle version does not support phased build action execution.
     * @throws org.gradle.tooling.exceptions.UnsupportedOperationConfigurationException
     *          When the target Gradle version does not support some requested configuration option.
     * @throws org.gradle.tooling.exceptions.UnsupportedBuildArgumentException When there is a problem with build arguments provided by {@link #withArguments(String...)}.
     * @throws BuildActionFailureException When one of the build actions fails with an exception.
     * @throws BuildCancelledException When the operation was cancelled before it completed successfully.
     * @throws BuildException On some failure executing the Gradle build.
     * @throws GradleConnectionException On some other failure using the connection.
     * @throws IllegalStateException When the connection has been closed or is closing.
     * @since 4.7
     */
    void run() throws GradleConnectionException, IllegalStateException;

    /**
     * Starts executing the build, passing the build result to the given handler when complete and individual action results to the respective handler when complete.
     * This method returns immediately, and the result is later passed to the given handler's {@link ResultHandler#onComplete(Object)} method.
     *
     * <p>If the operation fails, the handler's {@link ResultHandler#onFailure(GradleConnectionException)} method is called with the appropriate exception. See
     * {@link #run()} for a description of the various exceptions that the operation may fail with.
     *
     * @param handler The handler to supply the build result to. Individual action results are supplied to its respective handler.
     * @throws IllegalStateException When the connection has been closed or is closing.
     * @since 4.7
     */
    void run(ResultHandler<? super Void> handler) throws IllegalStateException;
}
