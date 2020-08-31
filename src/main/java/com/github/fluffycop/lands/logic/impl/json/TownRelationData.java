package com.github.fluffycop.lands.logic.impl.json;

import java.util.Set;

public class TownRelationData {
    private volatile String leaderUid;
    private volatile Set<String> memberUids;

    public TownRelationData(String leaderUid, Set<String> memberUids) {
        this.leaderUid = leaderUid;
        this.memberUids = memberUids;
    }

    public TownRelationData() { }

    public String getLeaderUid() {
        return leaderUid;
    }

    public Set<String> getMemberUids() {
        return memberUids;
    }

    public void setLeaderUid(String leaderUid) {
        this.leaderUid = leaderUid;
    }

    public void setMemberUids(Set<String> memberUids) {
        this.memberUids = memberUids;
    }
}
