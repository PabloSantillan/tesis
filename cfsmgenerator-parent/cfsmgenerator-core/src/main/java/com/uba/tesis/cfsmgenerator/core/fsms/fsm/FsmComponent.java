package com.uba.tesis.cfsmgenerator.core.fsms.fsm;

import com.uba.tesis.cfsmgenerator.core.fsms.fsm.collaborators.Block;

/**
 * 
 * @author Pablo Santillan
 *
 */
public abstract class FsmComponent {
	protected Block block;

	// getters and setters.
	
	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		if (block != null){
			block.addFsmComponent(this);
		} else {
			if (this.block != null){
				this.block.removeFsmComponent(this);
			}
		}
		this.block = block;		
	}
}
