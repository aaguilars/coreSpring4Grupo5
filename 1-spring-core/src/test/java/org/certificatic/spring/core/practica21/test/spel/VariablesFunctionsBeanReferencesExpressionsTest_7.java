package org.certificatic.spring.core.practica21.test.spel;

import java.util.List;

import org.apache.commons.math3.complex.Complex;
import org.certificatic.spring.core.practica19.javaconfig.bean.api.QuadraticEquationResult;
import org.certificatic.spring.core.practica21.spel.bean.Chicharronera;
import org.certificatic.spring.core.practica21.spel.bean.GuessNumber;
import org.certificatic.spring.core.practica21.spel.bean.Magician;
import org.certificatic.spring.core.practica21.spel.bean.MyBeanResolver;
import org.certificatic.spring.core.practica21.spel.configuration.ApplicationConfig;
import org.certificatic.spring.core.practica21.spel.model.Inventor;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.expression.AccessException;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VariablesFunctionsBeanReferencesExpressionsTest_7 {

	private static ExpressionParser spelParser = new SpelExpressionParser();

	private static StandardEvaluationContext springContext;

	private static ApplicationContext applicationContext;

	@BeforeClass
	public static void setUp() {
		applicationContext = new AnnotationConfigApplicationContext(ApplicationConfig.class);

		springContext = new StandardEvaluationContext();

		// asignar al 'springContext' el bean resolver 'MyBeanResolver' que se
		// encuentra en el application context
		springContext.setBeanResolver(applicationContext.getBean(MyBeanResolver.class));
	}

	@Test
	public void beanResolverTest() {

		Object nullObject = spelParser.parseExpression("@miBean").getValue(springContext, Object.class);

		Assert.assertNull(nullObject);

		log.info("nullObject: {}", nullObject);

		Inventor ivanInventor = spelParser.parseExpression("@inventor").getValue(springContext, Inventor.class);

		Assert.assertNotNull(ivanInventor);

		log.info("ivanInventor: {}", ivanInventor);
	}

	@Test
	public void variablesExpressionsTest() {

		log.info("variablesExpressionsTest -------------------");

		Magician magician = applicationContext.getBean(Magician.class);

		springContext.setVariable("magicNumber", magician.getInitialNumber()); // sólo
																				// analizar

		// defnir y obtener el valor de una expresión que acceda al bean
		// guessNumberBean definido en el aplication-context.xml // y recupere el
		// valor de la propiedad randomNumber
		Integer randomNumber = spelParser.parseExpression(
				"@guessNumberBean.randomNumber").getValue(springContext, Integer.class);

		springContext.setVariable("randomNumber", randomNumber); // sólo
																	// analizar

		// -------------------------------------

		Boolean isCorrectNumber = spelParser.parseExpression("#randomNumber == #magicNumber").getValue(springContext,
				Boolean.class); // sólo analizar

		Assert.assertNotNull(isCorrectNumber);
		Assert.assertTrue(isCorrectNumber);

		log.info("isCorrectNumber: {}", isCorrectNumber);

		log.info("magician.initialNumber: {}", magician.getInitialNumber());

		log.info("gessNumberBean.randomNumber: {}", randomNumber);
	}

	@Test
	@SneakyThrows
	public void functionsExpressionsTest() {

		log.info("functionsExpressionsTest -------------------");

		// analizar setVariable's 
		springContext.setVariable("a", 5);
		springContext.setVariable("b", 4);
		springContext.setVariable("c", -10);

		// registra una función llamada 'chicharronera' que invoque al método
		// 'calculate' de la clase Chicharronera
		springContext.registerFunction("chicharronera", 
				Chicharronera.class.getDeclaredMethod("calculate", 
				new Class[]{ double.class, double.class, double.class }));
		

		QuadraticEquationResult expectedResult = QuadraticEquationResult.builder().x1(new Complex(1.0697, 0.0))
				.x2(new Complex(-1.8697, 0.0)).build(); // sólo analiza

		// definir y obtener el valor de la expresión que invoque a la función
		// 'chicharronera' tomando como argumentos 
		// las variables 'a', 'b' y 'c'
		QuadraticEquationResult quadraticEquationResult = spelParser.parseExpression(
				"#chicharronera(#a, #b, #c)").getValue(springContext,
				QuadraticEquationResult.class);

		Assert.assertNotNull(quadraticEquationResult);

		Assert.assertEquals(expectedResult, quadraticEquationResult);

		log.info("quadraticEquationResult: {}", quadraticEquationResult);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void beanReferencesExpressionsTest() {

		log.info("beanReferencesExpressionsTest -------------------");

		GuessNumber guessNumber = spelParser.parseExpression("@guessNumberBean").getValue(springContext,
				GuessNumber.class); // solo analiza

		Assert.assertNotNull(guessNumber);

		log.info("guessNumber: {}", guessNumber);

		// -------------------------------------

		Inventor tesla = spelParser.parseExpression("@teslaBean").getValue(springContext, Inventor.class); // s�lo
																											// analiza

		Assert.assertNotNull(tesla);

		log.info("tesla: {}", tesla);

		// -------------------------------------

		Integer inventionsLength = spelParser.parseExpression("@teslaBean.inventions.length").getValue(springContext,
				int.class); // s�lo analiza

		Assert.assertNotNull(inventionsLength);

		log.info("inventionsLength: {}", inventionsLength);

		// -------------------------------------

		List<String> inventions = spelParser.parseExpression("@teslaBean.inventions").getValue(springContext,
				List.class); // s�lo analiza

		Assert.assertNotNull(inventions);

		log.info("inventions: {}", inventions);

		Assert.assertEquals(3, inventions.size());
	}

}
