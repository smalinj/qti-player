package com.qtitools.player.client.components.htmldnd;

public enum DragSlotLayout {
	HORIZONTAL, VERTICAL;
	
	public static DragSlotLayout fromDragServiceMode(DragMode dsm){
		if (dsm == DragMode.HORIZONTAL)
			return DragSlotLayout.HORIZONTAL;
		if (dsm == DragMode.VERTICAL)
			return DragSlotLayout.VERTICAL;
		
		return DragSlotLayout.HORIZONTAL;
	}
}
