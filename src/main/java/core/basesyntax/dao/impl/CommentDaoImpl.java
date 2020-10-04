package core.basesyntax.dao.impl;

import core.basesyntax.dao.CommentDao;
import core.basesyntax.model.Comment;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

@Log4j
public class CommentDaoImpl extends AbstractDao implements CommentDao {
    public CommentDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Comment create(Comment entity) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                log.debug("Transaction creation for " + entity.toString()
                        + " has been rollbacked.", e);
                transaction.rollback();
            }
            throw new RuntimeException("Can't insert Content entity");
        } finally {
            if (session != null) {
                session.close();
            }
        }
        log.debug("Entity " + entity.toString() + " created");
        return entity;
    }

    @Override
    public Comment get(Long id) {
        try (Session session = factory.openSession()) {
            return session.get(Comment.class, id);
        } catch (HibernateException e) {
            throw new RuntimeException("Can't geet entity id=" + id, e);
        }
    }

    @Override
    public List<Comment> getAll() {
        List<Comment> commentList = new ArrayList<>();
        try (Session session = factory.openSession()) {
            Query<Comment> getAllCommentQuery = session.createQuery("from Comment", Comment.class);
            commentList = getAllCommentQuery.getResultList();
        } catch (HibernateException e) {
            throw new RuntimeException("Can't get all comments", e);
        }
        return commentList;
    }

    @Override
    public void remove(Comment entity) {
        Transaction transaction;
        Session session = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            Comment comment = session.find(Comment.class, entity.getId());
            if (comment != null) {
                session.remove(comment);
                transaction.commit();
            }
        } catch (HibernateException e) {
            throw new RuntimeException("Can't remove comment id=" + entity.getId(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        log.debug("Entity " + entity.toString() + " has been removed");
    }
}
