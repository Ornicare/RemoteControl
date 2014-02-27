
public enum Status {
	Connected,
	Deconnected;
	
	@Override
	public String toString() {
		if(this==Connected) return "Connected";
		if(this==Deconnected) return "Deconnected";
		return null;
	}
}
