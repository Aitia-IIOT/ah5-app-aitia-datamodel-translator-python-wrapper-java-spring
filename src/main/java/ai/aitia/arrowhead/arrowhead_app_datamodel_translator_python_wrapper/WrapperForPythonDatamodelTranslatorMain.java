package ai.aitia.arrowhead.arrowhead_app_datamodel_translator_python_wrapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import ai.aitia.arrowhead.Constants;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan({ Constants.BASE_PACKAGE, Constants.COMMON_BASE_PACKAGE })
public class WrapperForPythonDatamodelTranslatorMain {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static void main(String[] args) {
		SpringApplication.run(WrapperForPythonDatamodelTranslatorMain.class, args);
	}
	
	//=================================================================================================
	// boilerplate

	//-------------------------------------------------------------------------------------------------
	protected WrapperForPythonDatamodelTranslatorMain() {
	}

}
