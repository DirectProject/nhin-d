using System.Linq;

namespace AdminMvc.Models
{
    public abstract class Repository<T>
    {
        public abstract T Get(long id);

        public abstract IQueryable<T> FindAll();
        public abstract T Add(T obj);
        public abstract void Update(T obj);
        public abstract void Delete(T obj);
    }
}