package org.clyze.persistent.model

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

abstract class ItemImpl implements Item {

	static final String ID_FIELD = "id"

	String getId() { this[ID_FIELD] }

	ItemImpl fromJSON(String json) {
		def map = new JsonSlurper().parseText(json)
		map.each { String key, Object value ->
			if (this.properties.containsKey(key)) {
				this[key] = value
			}
		}
		return this
	}

	String toJSON() { JsonOutput.toJson(toMap()) }

	Map<String, Object> toMap() {
		return properties.findAll { String key, Object value ->
			value != null && key != "class" && key != "ID_FIELD"
		}
	}
}
