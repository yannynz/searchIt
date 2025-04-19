package git.yannynz.searchIt.service;

import git.yannynz.searchIt.model.Company;

import java.util.List;

public interface WebSearchService {
    List<Company> searchCompaniesOnInternet(
        String produto,
        Integer raioKM,
        double latitude,
        double longitude
    );
}

