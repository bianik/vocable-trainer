package myProjects.vocableTrainer.model;

public class Vocable {
	private String phrase, translation;
	private int corrTries, falseTries;

	public Vocable() {
		super();
	}

	public Vocable(String phrase, String translation) {
		super();
		this.phrase = phrase;
		this.translation = translation;
		corrTries = 0;
		falseTries = 0;
	}
	
	// autogenerated Getters and Setters
	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}

	public int getCorrTries() {
		return corrTries;
	}

	public void setCorrTries(int corrTries) {
		this.corrTries = corrTries;
	}

	public int getFalseTries() {
		return falseTries;
	}

	public void setFalseTries(int falseTries) {
		this.falseTries = falseTries;
	}

	// Increment corrTries and falseTries
	public void incCorrTries() {
		corrTries++;
	}
	
	public void incFalseTries() {
		falseTries++;
	}
	
	// self implemented function
	public boolean compareTo(Vocable other) {
		return phrase.equals(other.getPhrase()) && translation.contentEquals(other.getTranslation());
	}

	// auto generated functions
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((phrase == null) ? 0 : phrase.hashCode());
		result = prime * result + ((translation == null) ? 0 : translation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vocable other = (Vocable) obj;
		if (phrase == null) {
			if (other.phrase != null)
				return false;
		} else if (!phrase.equals(other.phrase))
			return false;
		if (translation == null) {
			if (other.translation != null)
				return false;
		} else if (!translation.equals(other.translation))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Vocable [phrase=" + phrase + ", translation=" + translation + "]";
	}
}
