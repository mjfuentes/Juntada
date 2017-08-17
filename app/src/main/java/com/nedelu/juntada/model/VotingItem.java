package com.nedelu.juntada.model;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class VotingItem {

    private PollOption option;
    private Boolean voted;

    public VotingItem (PollOption option){
        this.option = option;
        this.voted = false;
    }

    public PollOption getOption() {
        return option;
    }

    public void setOption(PollOption option) {
        this.option = option;
    }

    public Boolean getVoted() {
        return voted;
    }

    public void setVoted(Boolean voted) {
        this.voted = voted;
    }

}
