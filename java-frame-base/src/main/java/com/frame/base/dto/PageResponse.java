package com.frame.base.dto;

import java.util.List;

public record PageResponse<T>(List<T> rows,
                              long total,
                              int totalPages,
                              int size,
                              int page
                              )
{
}
