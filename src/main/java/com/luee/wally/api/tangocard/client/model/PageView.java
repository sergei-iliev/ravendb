/**
* NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (4.2.3).
* https://openapi-generator.tech
* Do not edit the class manually.
*
* Code generation script: src/main/openapi-generator/generate-marqeta-models.sh
*/
package com.luee.wally.api.tangocard.client.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
            
/**
 * PageView
 */


public class PageView  implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("elementsPerBlock")
    private Integer elementsPerBlock;
    @JsonProperty("number")
    private Integer number;
    @JsonProperty("resultCount")
    private Integer resultCount;
    @JsonProperty("totalCount")
    private Long totalCount;
    
    
    public PageView() {
	
	}
    
	public Integer getElementsPerBlock() {
		return elementsPerBlock;
	}
	public void setElementsPerBlock(Integer elementsPerBlock) {
		this.elementsPerBlock = elementsPerBlock;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public Integer getResultCount() {
		return resultCount;
	}
	public void setResultCount(Integer resultCount) {
		this.resultCount = resultCount;
	}
	public Long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}
    
    
}

