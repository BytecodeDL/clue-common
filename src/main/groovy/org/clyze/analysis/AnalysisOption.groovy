package org.clyze.analysis

import groovy.transform.AutoClone
import groovy.transform.CompileStatic

import static groovy.transform.AutoCloneStyle.COPY_CONSTRUCTOR

@AutoClone(style = COPY_CONSTRUCTOR)
@CompileStatic
class AnalysisOption<T> {
	
	/**
	 * The id of the option as used internally by the code (e.g. by the preprocessor, the web form, etc)
	 */
	String id

	/**
	 * The name of the option (for the end-user)
	 */
	String name

	/**
	 * Shorthand version of the above name (for cli)
	 */
	String optName

	/**
	 * The description of the option (for the end-user)
	 */
	String description

	/**
	 * The value of the option
	 */
	T value = null

	/**
	 * An optional set of valid values
	 */
	Set<T> validValues

	/**
	 * Indicate whether the option accepts many values
	 */
	boolean multipleValues = false

	/**
	 * The name of the option's arg value. If null, the option does not take arguments (it is a flag/boolean option)
	 */
	String argName

	/**
	 * The type of the (file) argument
	 */
	InputType argInputType

	/**
	 * Indicates whether the option can be specified by the user in the command line interface
	 */
	boolean cli = true

	/**
	 * Indicates whether the option is a mandatory one
	 */
	boolean isMandatory = false

	/**
	 * Indicates whether the option affects the cacheID generation
	 */
	boolean forCacheID = false

	/**
	 * Indicates whether the option affects the preprocessor
	 */
	boolean forPreprocessor = false

	/**
	 * The group containing this option (e.g., "reflection"), null for none.
	 */
	String group = null

	String toString() { "$id=$value" }
}
