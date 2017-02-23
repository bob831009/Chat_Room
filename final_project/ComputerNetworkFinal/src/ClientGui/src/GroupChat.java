

import java.util.ArrayList;

class GroupChat {
	String groupName;
	ArrayList<String> groupMember = new ArrayList<String>();
	public GroupChat(String groupName, String[] groupMember) {
		this.groupName = groupName;
		for (String member : groupMember)
			this.groupMember.add(member);
	}
	public String getGroupName() {
		return groupName;
	}
	public String[] getGroupMember() {
		String[] member = groupMember.toArray(new String[groupMember.size()]);
		return member;
	}
	public void modifyGroupMember(String[] modifyMember) {
		for (String member : modifyMember) {
			if (this.groupMember.contains(member))
				this.removeGroupMember(member);
			else
				this.addGroupMember(member);
		}
	}
	public void addGroupMember(String newMember) {
		this.groupMember.add(newMember);
		return;
	}
	public void removeGroupMember(String removeMember) {
		this.groupMember.remove(removeMember);
		return;
	}
}
