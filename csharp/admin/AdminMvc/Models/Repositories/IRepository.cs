using System.Linq;

namespace AdminMvc.Models.Repositories
{
    public interface IRepository<T>
    {
        T Get(long id);
        IQueryable<T> FindAll();
        T Add(T obj);
        void Update(T obj);
        void Delete(T obj);
    }
}