package git.yannynz.searchIt.service;
import git.yannynz.searchIt.model.Company;
import java.util.List;
public interface CompanyService {
  List<Company> searchCompanies(String produto, Integer raioKM, double lat, double lng);
}

