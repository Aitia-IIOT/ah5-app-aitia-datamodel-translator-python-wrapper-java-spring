package ai.aitia.arrowhead.data_saver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import ai.aitia.arrowhead.Constants;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan({ Constants.BASE_PACKAGE, Constants.COMMON_BASE_PACKAGE })
public class DataSaverMain {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static void main(String[] args) {
		SpringApplication.run(DataSaverMain.class, args);
	}
	
	//=================================================================================================
	// boilerplate

	//-------------------------------------------------------------------------------------------------
	protected DataSaverMain() {
	}
}
