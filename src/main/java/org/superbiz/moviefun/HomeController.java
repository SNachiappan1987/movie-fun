package org.superbiz.moviefun;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@Transactional
public class HomeController {

    private MoviesBean moviesBean;
    public HomeController(MoviesBean moviesBean){
        this.moviesBean=moviesBean;
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model){
        moviesBean.addMovie(new Movie("Wedding Crashers", "David Dobkin", "Comedy", 7, 2005));
        moviesBean.addMovie(new Movie("Starsky & Hutch", "Todd Phillips", "Action", 6, 2004));
        model.put("movies",moviesBean.getMovies());
        return "setup";
    }

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/moviefun")
    public String moviefun(){
        return "moviefun";
    }

}

