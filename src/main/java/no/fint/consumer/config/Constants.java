
package no.fint.consumer.config;

public enum Constants {
;

    public static final String COMPONENT = "pwfa";
    public static final String COMPONENT_CONSUMER = COMPONENT + " consumer";
    public static final String CACHE_SERVICE = "CACHE_SERVICE";

    
    public static final String CACHE_INITIALDELAY_DOG = "${fint.consumer.cache.initialDelay.dog:900000}";
    public static final String CACHE_FIXEDRATE_DOG = "${fint.consumer.cache.fixedRate.dog:900000}";
    
    public static final String CACHE_INITIALDELAY_OWNER = "${fint.consumer.cache.initialDelay.owner:910000}";
    public static final String CACHE_FIXEDRATE_OWNER = "${fint.consumer.cache.fixedRate.owner:900000}";
    

}
