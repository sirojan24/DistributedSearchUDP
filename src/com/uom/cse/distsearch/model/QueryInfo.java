package com.uom.cse.distsearch.model;

public class QueryInfo {
	private NodeInfo sourceNodeInfo;
	
	private NodeInfo senderNodeInfo;
	
	private String filename;
	
	long timestamp;
	
	public QueryInfo(NodeInfo sourceNodeInfo, NodeInfo senderNodeInfo, String filename, long timestamp) {
		this.sourceNodeInfo = sourceNodeInfo;
		this.senderNodeInfo = senderNodeInfo;
		this.timestamp = timestamp;
		this.filename = filename;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public NodeInfo getSourceNodeInfo() {
		return sourceNodeInfo;
	}
	public void setSourceNodeInfo(NodeInfo sourceNodeInfo) {
		this.sourceNodeInfo = sourceNodeInfo;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}

	public NodeInfo getSenderNodeInfo() {
		return senderNodeInfo;
	}

	public void setSenderNodeInfo(NodeInfo senderNodeInfo) {
		this.senderNodeInfo = senderNodeInfo;
	}
	
}
