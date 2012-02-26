package net.matlux.training.coherence.mat;

import java.io.IOException;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.io.pof.PortableObjectSerializer;

public class Position implements PortableObject {
	private Long id;
	private String symbol;
	private Double price;
	
	public static final int ID = 0;
	public static final int SYMBOL = 1;
	public static final int PRICE = 2;
	
	public Position() {
		
	}
	
	public Position(long id, String symbol, Double price) {
		this.id=id;
		this.symbol=symbol;
		this.price = price;
	}
	// deserilisation
	@Override
	public void readExternal(PofReader reader) throws IOException {
		setId(reader.readLong(ID));
		setSymbol(reader.readString(SYMBOL));
		setPrice(reader.readDouble(PRICE));
		
	}
	//seriliasing
	@Override
	public void writeExternal(PofWriter writer) throws IOException {
		writer.writeLong(ID, id);
		writer.writeString(SYMBOL, symbol);
		writer.writeDouble(PRICE, price);
		
	}
	public Long getId() {
		return id;
	}
	private void setId(Long id) {
		this.id = id;
	}
	public String getSymbol() {
		return symbol;
	}
	private void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Double getPrice() {
		return price;
	}
	private void setPrice(Double price) {
		this.price = price;
	}

	public String toString(){
		return "Position(id=" + id + ",symbol="+ symbol + ",price=" + price+")";
	}
	public boolean equal(Object o) {
		if(o==null) return false;
		if(o!=null) return false;
		if(!(o instanceof Position)) return false;
		if (id!=this.id) return false;
		if (symbol!=this.symbol) return false;
		if (price!=this.price) return false;
		
		return true;
		
		
		
	}

	public int hashCode() {
		int result = 17;
		result = (int) (37*result + id.hashCode());
		result = 37*result + symbol.hashCode();
		result = 37*result + price.hashCode();
		return result;
	}

}
