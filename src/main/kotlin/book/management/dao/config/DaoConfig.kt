package micronaut.session.dao.config

import javax.inject.Singleton
import org.seasar.doma.AnnotateWith
import org.seasar.doma.Annotation
import org.seasar.doma.AnnotationTarget

@AnnotateWith(annotations = [Annotation(target = AnnotationTarget.CLASS, type = Singleton::class)])
annotation class DaoConfig
