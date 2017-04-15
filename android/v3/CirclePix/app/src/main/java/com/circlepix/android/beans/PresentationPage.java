package com.circlepix.android.beans;

public class PresentationPage {
	
	private int pageId;
	private int pageOrder;
	private String name;
	private int audioFile;
	private String field;
	private boolean active = true;
	
	public PresentationPage(int pageId, int pageOrder, String name,
			int audioFile, String field, boolean active) {
		super();
		this.pageId = pageId;
		this.pageOrder = pageOrder;
		this.name = name;
		this.audioFile = audioFile;
		this.field = field;
		this.active = active;
	}

	public int getPageId() {
		return pageId;
	}

	public void setPageId(int pageId) {
		this.pageId = pageId;
	}

	public int getPageOrder() {
		return pageOrder;
	}

	public void setPageOrder(int pageOrder) {
		this.pageOrder = pageOrder;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAudioFile() {
		return audioFile;
	}

	public void setAudioFile(int audioFile) {
		this.audioFile = audioFile;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
}
