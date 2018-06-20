package org.clyze.analysis

import groovy.transform.AutoClone
import static groovy.transform.AutoCloneStyle.COPY_CONSTRUCTOR

@AutoClone(style = COPY_CONSTRUCTOR)
class AnalysisOption<T> {
	
	/**
	 * The id of the option as used internally by the code (e.g. by the preprocessor, the web form, etc)
	 */
	String id

	/**
	 * The name of the option (for the end-user)
	 */
	String name = null

	/**
	 * Shorthand version of the above name (for cli)
	 */
	String optName = null

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
	Set<T> validValues = null

	/**
	 * Indicate whether the option accepts many values
	 */
	boolean multipleValues = false

	/**
	 * The type of the values
	 */
	InputType valueType = null

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
	 * Indicates whether the option can be specified by the user in the web UI
	 */
	UI webUI = null

	/**
	 * Indicates whether the option can be specified by the user in the command line interface
	 */
	boolean cli = true

	/**
	 * The name of the option's arg value. If null, the option does not take arguments (it is a flag/boolean option)
	 */
	String argName = null

	/**
	 * Indicates whether the option is "advanced". Advanced options are treated differently by the UIs
	 */
	boolean isAdvanced = false

	/**
	 * Indicates whether the option may affect the facts generated by Doop.
	 */
	boolean changesFacts = false

	/**
	 * Indicates whether the option is a file
	 */
	boolean isFile = false

	/**
	 * Indicates whether the option is a directoy
	 */
	boolean isDir = false

	/**
	 * Indicates whether the option is a non-standard flag
	 */
	boolean nonStandard = false

	String toString() { "$id=$value" }

	boolean acceptsMultipleInputs() {
		this.multipleValues && this.valueType && (this.valueType instanceof InputType)
	}
}
